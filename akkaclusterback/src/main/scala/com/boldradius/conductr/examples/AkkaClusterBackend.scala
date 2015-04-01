package com.boldradius.conductr.examples

import java.net.URL

import akka.actor._
import akka.cluster.{Member, Cluster}
import akka.cluster.ClusterEvent.{MemberRemoved, UnreachableMember, MemberEvent, MemberUp}
import com.typesafe.conductr.bundlelib.akka.{LocationService, ClusterProperties}
import com.typesafe.conductr.bundlelib.akka.ImplicitConnectionContext
import com.typesafe.conductr.bundlelib.scala.{Env, StatusService}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import akka.pattern.pipe

import scala.concurrent.Await
import scala.concurrent.duration._


/**
 * 8090
 */
object AkkaClusterBackend extends App with LazyLogging {

//  ClusterProperties.initialize()

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

  if( config.getString("akka.remote.netty.tcp.hostname") == "127.0.0.1"){
    sys.props ++= List("akka.cluster.seed-nodes.0" -> "akka.tcp://AkkaConductRExamplesClusterSystem@127.0.0.1:8089")
  }


  val system = ActorSystem("AkkaConductRExamplesClusterSystem", config)

  system.actorOf(Props(classOf[AkkaClusterBackend]))




}





class AkkaClusterBackend extends Actor with ActorLogging with ImplicitConnectionContext  {

  val cluster = Cluster(context.system)


  import context.dispatcher

  override def preStart(): Unit ={
    val seed = Await.result(LocationService.lookup("/seed"), 20 seconds)

    println("------------- HERE")

    log.info("************  seed:  " + seed)

    LocationService.lookup("/seed").pipeTo(self)


  }



  override def postStop(): Unit = cluster.unsubscribe(self)


  override def receive: Receive =
    initial

  private def initial: Receive = {
    case Some(someService: String) =>
      // We now have the seed, join the cluster

      val url = new URL(someService)

      log.info("---------seed url: " + someService)
      log.info("---------URL: " + url.toString)
      log.info("---------URL: " + url.getHost)

      cluster.join(Address("tcp", "AkkaConductRExamplesClusterSystem", url.getHost, url.getPort))



      cluster.subscribe(self, classOf[MemberEvent])
      context.become(service)

    case None =>
      log.info("#############   no service found")
      self ! (if (Env.isRunByConductR) PoisonPill else Some("http://127.0.0.1:9000"))
  }

  private def service: Receive = {
    case MemberUp(member) =>
      log.info("-----------Member is Up: {}", member.address)
      register(member)
    case UnreachableMember(member) =>
      log.info("----------Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("----------Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore

    case Trivial => sender() ! "AkkaClusterBackend success"

  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend")) {
      import com.typesafe.conductr.bundlelib.scala.ConnectionContext.Implicits._
      log.info("--------front end is registered, sending BackendRegistration")
      context.actorSelection(RootActorPath(member.address) / "user" / "akkaClusterFrontend") ! BackendRegistration
      StatusService.signalStartedOrExit()
    }

}



