package com.boldradius.conductr.examples

import java.net.URL

import akka.actor._
import akka.cluster.{Member, Cluster}
import akka.cluster.ClusterEvent.{MemberRemoved, UnreachableMember, MemberEvent, MemberUp}
import akka.io.IO
import com.typesafe.conductr.bundlelib.akka.{LocationService, ClusterProperties}
import com.typesafe.conductr.bundlelib.akka.ImplicitConnectionContext
import com.typesafe.conductr.bundlelib.scala.ConnectionContext.Implicits._
import com.typesafe.conductr.bundlelib.scala.{Env, StatusService}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import akka.pattern.pipe
import spray.can.Http

import spray.http._
import spray.client.pipelining._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success}


/**
 * 8090
 */
object AkkaClusterBackend extends App with LazyLogging {

  ClusterProperties.initialize()

  val config =  ConfigFactory.parseString("akka.cluster.roles = [backend]").withFallback(ConfigFactory.load())

  logger.info("AkkaClusterBackend akka.remote.netty.tcp.hostname: " +config.getString("akka.remote.netty.tcp.hostname"))
  logger.info("AkkaClusterBackend akka.remote.netty.tcp.port: " +config.getString("akka.remote.netty.tcp.port"))
  logger.info("AkkaClusterBackend akka.cluster.seed-nodes: " +config.getList("akka.cluster.seed-nodes"))

  logger.info(s"AKKA_REMOTE_HOST ${sys.env.get("AKKA_REMOTE_HOST")}")


  logger.info(s"bundleHostIp ${sys.env.get("BUNDLE_HOST_IP")}")
  logger.info(s"bundleSystem ${sys.env.get("BUNDLE_SYSTEM")}")
  logger.info(s"akkaRemoteHostProtocol ${sys.env.get("AKKA_REMOTE_HOST_PROTOCOL")}")
  logger.info(s"akkaRemoteHostPort ${sys.env.get("AKKA_REMOTE_HOST_PORT")}")
  logger.info(s"akkaRemoteOtherProtocolsConcat ${sys.env.get("AKKA_REMOTE_OTHER_PROTOCOLS")}")
  logger.info(s"akkaRemoteOtherIpsConcat ${sys.env.get("AKKA_REMOTE_OTHER_IPS")}")
  logger.info(s"akkaRemoteOtherPortsConcat ${sys.env.get("AKKA_REMOTE_OTHER_PORTS")}")


  logger.info(s"SEED NODES ${sys.props.get("akka.cluster.seed-nodes.0")}")


  if( !Env.isRunByConductR ){
    sys.props ++= List("akka.cluster.seed-nodes.0" -> "akka.tcp://AkkaConductRExamplesClusterSystem@127.0.0.1:8089")
  }

  val system = ActorSystem("AkkaConductRExamplesClusterSystem", config)

  system.actorOf(Props(classOf[AkkaClusterBackend]))

}





class AkkaClusterBackend extends Actor with ActorLogging  {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberEvent])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {

    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
      register(member)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore

    case Trivial => sender() ! "AkkaClusterBackend success"
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend")) {
      log.info("front end is registered, sending BackendRegistration")
      context.actorSelection(RootActorPath(member.address) / "user" / "akkaClusterFrontend") ! BackendRegistration
      StatusService.signalStartedOrExit()
    }

}


