package com.boldradius.conductr.examples

import akka.actor._
import akka.io.IO
import akka.util.Timeout
import akka.actor.ActorRef
import akka.cluster.Cluster
import com.typesafe.conductr.bundlelib.akka.ClusterProperties
import com.typesafe.conductr.bundlelib.scala.StatusService
import com.typesafe.conductr.bundlelib.scala.ConnectionContext.Implicits._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.routing.HttpServiceActor
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.pattern.ask

import scala.util.{Failure, Success}


/**
 * 8089
 */
object AkkaClusterFrontend extends App with LazyLogging {

  ClusterProperties.initialize()

  val config = ConfigFactory.parseString("akka.cluster.roles = [frontend]").withFallback(ConfigFactory.load())

  logger.info("AkkaClusterFrontend akka.remote.netty.tcp.hostname: " + config.getString("akka.remote.netty.tcp.hostname"))
  logger.info("AkkaClusterFrontend akka.remote.netty.tcp.port: " + config.getString("akka.remote.netty.tcp.port"))
  logger.info("AkkaClusterFrontend akka.cluster.seed-nodes: " + config.getList("akka.cluster.seed-nodes"))


  logger.info(s"AKKA_REMOTE_HOST ${sys.env.get("AKKA_REMOTE_HOST")}")

  logger.info(s"bundleHostIp ${sys.env.get("BUNDLE_HOST_IP")}")
  logger.info(s"bundleSystem ${sys.env.get("BUNDLE_SYSTEM")}")
  logger.info(s"akkaRemoteHostProtocol ${sys.env.get("AKKA_REMOTE_HOST_PROTOCOL")}")
  logger.info(s"akkaRemoteHostPort ${sys.env.get("AKKA_REMOTE_HOST_PORT")}")
  logger.info(s"akkaRemoteOtherProtocolsConcat ${sys.env.get("AKKA_REMOTE_OTHER_PROTOCOLS")}")
  logger.info(s"akkaRemoteOtherIpsConcat ${sys.env.get("AKKA_REMOTE_OTHER_IPS")}")
  logger.info(s"akkaRemoteOtherPortsConcat ${sys.env.get("AKKA_REMOTE_OTHER_PORTS")}")
  logger.info(s"SPRAY_HTTP_BIND_IP ${sys.env.get("SPRAY_HTTP_BIND_IP")}")
  logger.info(s"SPRAY_HTTP_BIND_PORT ${sys.env.get("SPRAY_HTTP_BIND_PORT")}")


  val http: (String, Int) =
    sys.env.get("SPRAY_HTTP_BIND_IP").flatMap(ip =>
      sys.env.get("SPRAY_HTTP_BIND_PORT").map(port => (ip, port.toInt))
    ). fold[(String,Int)]{
      sys.props ++= List("akka.cluster.seed-nodes.0" -> "akka.tcp://AkkaConductRExamplesClusterSystem@127.0.0.1:8089")
      ("127.0.0.1", 8095)
    }(identity)




  implicit val system = ActorSystem("AkkaConductRExamplesClusterSystem", config)


  val frontEnd = system.actorOf(Props(new AkkaClusterFrontend), name = "akkaClusterFrontend")

  StatusService.signalStartedOrExit()

  Cluster(system) registerOnMemberUp {
    implicit val timeout = Timeout(5.seconds)

    val service = system.actorOf(Props(classOf[FrontEndHttpActor], frontEnd), "akka-cluster-http-actor")

    IO(Http) ? Http.Bind(service, interface = http._1, port = http._2)

  }
}


trait FrontEndHttpRoute extends HttpServiceActor {
  implicit val ec: ExecutionContext

  implicit val timeout = Timeout(10 seconds)

  import MarshallingSupport._

  val frontEndActor: ActorRef

  def route = {
    get {
      path("akkacluster") {
        onComplete((frontEndActor ? Trivial).mapTo[String]) {
          case Success(r) => complete(r)
          case Failure(t) => t.printStackTrace; complete(t.getMessage)
        }
      }
    }
  }
}

class FrontEndHttpActor(fEndActor: ActorRef)
  extends HttpServiceActor
  with FrontEndHttpRoute {
  implicit val ec = context.dispatcher
  override val frontEndActor = fEndActor

  def receive = runRoute(route)
}


class AkkaClusterFrontend() extends Actor with ActorLogging {


  implicit val timeout = Timeout(5 seconds)

  var backends = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  def receive = {
    case Trivial if backends.isEmpty =>
      sender() ! "Service unavailable, try again later"

    case Trivial =>
      jobCounter += 1
      backends(jobCounter % backends.size) forward Trivial

    case BackendRegistration if !backends.contains(sender()) =>
      log.info("backend registered, adding to backends")
      context watch sender()
      backends = backends :+ sender()

    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}
