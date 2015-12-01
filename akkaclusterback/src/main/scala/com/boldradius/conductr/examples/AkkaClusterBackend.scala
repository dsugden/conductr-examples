package com.boldradius.conductr.examples

import java.net.URL

import akka.actor._
import akka.cluster.{Member, Cluster}
import akka.cluster.ClusterEvent._
import akka.io.IO
import com.typesafe.conductr.bundlelib.akka.{ConnectionContext, StatusService, Env}
//import com.typesafe.conductr.bundlelib.akka.{Env, ConnectionContext}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.conductr.bundlelib.scala.ConnectionContext.Implicits.global
import com.typesafe.conductr.bundlelib.akka.Env
import com.typesafe.scalalogging.LazyLogging
import akka.pattern.pipe
import spray.can.Http

import spray.http._
import spray.client.pipelining._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object AkkaClusterBackend extends App with LazyLogging {


  val config = Env.asConfig.withFallback(ConfigFactory.load())
  val systemName = sys.env.getOrElse("BUNDLE_SYSTEM", "AkkaConductRExamplesClusterSystem")

  logger.info("AkkaClusterBackend akka.remote.netty.tcp.hostname: " +config.getString("akka.remote.netty.tcp.hostname"))
  logger.info("AkkaClusterBackend akka.remote.netty.tcp.port: " +config.getString("akka.remote.netty.tcp.port"))
  logger.info("AkkaClusterBackend akka.cluster.seed-nodes: " +config.getList("akka.cluster.seed-nodes"))

  logger.info(s"AkkaClusterBackend AKKA_REMOTE_HOST ${sys.env.get("AKKA_REMOTE_HOST")}")

  implicit val system = ActorSystem(systemName, config.withFallback(ConfigFactory.load()))

  Cluster(system).registerOnMemberUp {
    system.actorOf(Props(classOf[AkkaClusterBackend], config))

    logger.info("AkkaClusterBackend registerOnMemberUp ")
    implicit val cc = ConnectionContext()
    StatusService.signalStartedOrExit()
  }
}





class AkkaClusterBackend(config: Config) extends Actor with ActorLogging  {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self,
    initialStateMode = InitialStateAsEvents,
    classOf[MemberUp],
    classOf[UnreachableMember],
    classOf[MemberRemoved],
    classOf[MemberExited],
    classOf[LeaderChanged]
  )
  override def postStop(): Unit = cluster.unsubscribe(self)

  log.info("************ AkkaClusterBackend Actor")

  def receive = {

    case MemberUp(member) =>
      log.info("AkkaClusterBackend Member is Up: {}", member.address)
      register(member)
    case UnreachableMember(member) =>
      log.info("AkkaClusterBackend Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("AkkaClusterBackend Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore

    case Job(name) =>
      log.info("AkkaClusterBackend Job " + name)
      sender() ! s"AkkaClusterBackend success from host ${sys.env.get("BUNDLE_HOST_IP")} job:" +
        name +" password= " + config.getString("password")
  }

  def register(member: Member): Unit ={

    log.info(s"AkkaClusterBackend register:member $member" )

    if (member.hasRole("frontend")) {
      log.info("AkkaClusterBackend front end is registered, sending BackendRegistration")
      context.actorSelection(RootActorPath(member.address) / "user" / "akkaClusterFrontend") ! BackendRegistration
    }

  }

}


