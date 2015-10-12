package com.boldradius.conductr.examples

import akka.actor._
import akka.io.IO
import akka.util.Timeout
import akka.actor.ActorRef
import akka.cluster.Cluster
import com.typesafe.conductr.bundlelib.akka.Env
import com.typesafe.conductr.bundlelib.scala.StatusService
import com.typesafe.config.ConfigFactory
import com.typesafe.conductr.bundlelib.scala.ConnectionContext.Implicits.global
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.routing.HttpServiceActor
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.pattern.ask

import scala.util.{Failure, Success}


/**
 * This app creates an akka cluster and provides an http interface.
 */
object AkkaClusterFrontend extends App with LazyLogging {

  val config = Env.asConfig
  val systemName = sys.env.getOrElse("BUNDLE_SYSTEM", "MyApp1")

//  val config = ConfigFactory.parseString("akka.cluster.roles = [frontend]").withFallback(ConfigFactory.load())

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

  /**
   * Get the Http config from conductR
   */
  val http: (String, Int) =
    sys.env.get("SPRAY_HTTP_BIND_IP").flatMap(ip =>
      sys.env.get("SPRAY_HTTP_BIND_PORT").map(port => (ip, port.toInt))
    ). fold[(String,Int)]{
      ("127.0.0.1", 8095) // run locally
    }(identity)

  logger.info(s"SEED NODES ${sys.props.get("akka.cluster.seed-nodes.0")}")

  implicit val system = ActorSystem("AkkaConductRExamplesClusterSystem", config)

  // start the frontend actor
  val frontEndActor = system.actorOf(Props(new AkkaClusterFrontend), name = "akkaClusterFrontend")

  Cluster(system).registerOnMemberUp {

    // start Spray http service
    implicit val timeout = Timeout(10 seconds)
    val frontEndHttpService = system.actorOf(Props(classOf[FrontEndHttpActor], frontEndActor), "akka-cluster-http-actor")
    IO(Http) ? Http.Bind(frontEndHttpService, interface = http._1, port = http._2)

    // notify conductR
//    implicit val cc = ConnectionContext()
    StatusService.signalStartedOrExit()
  }
}

/**
 * Spray route
 */
trait FrontEndHttpRoute extends HttpServiceActor {
  implicit val ec: ExecutionContext

  implicit val timeout = Timeout(10 seconds)

  import MarshallingSupport._

  val frontEndActor: ActorRef

  def route = {
    get {
      path("akkacluster") {

        onComplete((frontEndActor ? Job("akkacluster")).mapTo[String]) {
          case Success(r) => complete(r)
          case Failure(t) => t.printStackTrace; complete(t.getMessage)
        }
      }
    }
  }
}

/**
 * Actor to handle HttpRequests. Fowards to AkkaClusterFrontend
 * @param fEndActor
 */
class FrontEndHttpActor(fEndActor: ActorRef)
  extends HttpServiceActor
  with FrontEndHttpRoute {
  implicit val ec = context.dispatcher
  override val frontEndActor = fEndActor
  def receive = runRoute(route)
}


/**
 * This actor participates in the cluster, delegating Jobs from web to the
 * backend worked actors
 */
class AkkaClusterFrontend() extends Actor with ActorLogging {

  log.info("new AkkaClusterFrontend()")

  implicit val timeout = Timeout(5 seconds)

  def receive = service(IndexedSeq.empty[ActorRef],0)

  def service(backends:IndexedSeq[ActorRef], jobCounter:Int):Receive = {

    case Job(name) if backends.isEmpty =>
      sender() ! "Service unavailable, try again later"

    case j@Job(name) =>
      backends(jobCounter % backends.size) forward j

    case BackendRegistration if !backends.contains(sender()) =>
      log.info("backend registered, adding to backends")
      context watch sender()
      context.become(service(backends :+ sender(), jobCounter))

    case Terminated(a) =>
      context.become(service(backends.filterNot(_ == a), jobCounter))
  }
}
