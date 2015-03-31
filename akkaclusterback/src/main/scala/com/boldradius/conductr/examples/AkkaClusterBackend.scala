package com.boldradius.conductr.examples

import akka.actor._
import akka.cluster.{Member, Cluster}
import akka.cluster.ClusterEvent.{MemberRemoved, UnreachableMember, MemberEvent, MemberUp}
import com.typesafe.conductr.bundlelib.akka.ClusterProperties
import com.typesafe.conductr.bundlelib.scala.StatusService
import com.typesafe.conductr.bundlelib.scala.ConnectionContext.Implicits._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging



/**
 * 8090
 */
object AkkaClusterBackend extends App with LazyLogging {

  val config =  ConfigFactory.parseString("akka.cluster.roles = [backend]").withFallback(ConfigFactory.load())


  logger.info("AkkaClusterBackend akka.remote.netty.tcp.hostname: " +config.getString("akka.remote.netty.tcp.hostname"))
  logger.info("AkkaClusterBackend akka.remote.netty.tcp.port: " +config.getString("akka.remote.netty.tcp.port"))
  logger.info("AkkaClusterBackend akka.cluster.seed-nodes: " +config.getList("akka.cluster.seed-nodes"))

  ClusterProperties.initialize()

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



