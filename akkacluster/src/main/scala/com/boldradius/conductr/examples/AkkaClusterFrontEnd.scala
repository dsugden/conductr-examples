package com.boldradius.conductr.examples

import akka.actor._
import akka.cluster.ClusterEvent.{MemberUp, UnreachableMember, MemberEvent, InitialStateAsEvents}
import akka.io.IO
import akka.routing.FromConfig
import akka.util.Timeout
import akka.util.Timeout
import akka.actor.ActorRef
import akka.actor.ActorContext
import akka.cluster.Cluster
import com.boldradius.conductr.examples.Trivial
import com.typesafe.config.ConfigFactory
import spray.can.Http
import spray.routing.HttpServiceActor
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.pattern.ask

import scala.util.{Failure, Success}


/**
  * 8089
  */
object AkkaClusterFrontend extends App {


  val httpHost = if (args.isEmpty) "localhost" else args(0)
  val httpPort = if (args.isEmpty || args.length == 1) 8080 else args(1).toInt


  val config = ConfigFactory.parseString("akka.cluster.roles = [frontend]").withFallback(ConfigFactory.load("frontend"))

  implicit val system = ActorSystem("AkkaConductRExamplesClusterSystem", config)

  val frontEnd = system.actorOf(Props(new AkkaClusterFrontend ), name = "akkaClusterFrontend")

  Cluster(system) registerOnMemberUp {
    implicit val timeout = Timeout(5.seconds)

    val service = system.actorOf( Props( classOf[FrontEndHttpActor],frontEnd), "akka-cluster-http-actor")

    IO(Http) ? Http.Bind(service, interface = httpHost, port = httpPort)
  }
}



trait FrontEndHttpRoute extends HttpServiceActor{
  implicit val ec: ExecutionContext

  implicit val timeout = Timeout(10 seconds)

  import MarshallingSupport._

  val frontEndActor:ActorRef

  def route = {
      get{
        path("akkacluster"){
          onComplete((frontEndActor ? Trivial).mapTo[String]){
            case Success(r) => complete(r)
            case Failure(t) => t.printStackTrace; complete(t.getMessage)
          }
        }
      }
  }
}

class FrontEndHttpActor(fEndActor:ActorRef)
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
      log.info("----------------  backend registered")
      context watch sender()
      backends = backends :+ sender()

    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}
