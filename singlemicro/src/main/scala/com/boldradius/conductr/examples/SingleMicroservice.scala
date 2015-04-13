package com.boldradius.conductr.examples

import akka.actor.{ActorSystem, Props, ActorRef}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import spray.routing._

import scala.concurrent.ExecutionContext
import spray.can.Http
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

import com.typesafe.conductr.bundlelib.scala._
import com.typesafe.conductr.bundlelib.scala.ConnectionContext.Implicits._



class HttpActor()
  extends HttpServiceActor
  with SingleMicroserviceRoute {
  implicit val ec = context.dispatcher
  def receive = runRoute(route)
}

trait SingleMicroserviceRoute extends HttpService {
  implicit val ec: ExecutionContext

  import MarshallingSupport._

  def route = {
    get {
      path("singlemicro") {
        complete("singlemicro Success")
      }
    }
  }
}


object SingleMicroservice extends App with LazyLogging{

  implicit val system = ActorSystem("MicroServiceSystem")

  val service = system.actorOf( Props( classOf[HttpActor]), "http-actor")

  implicit val timeout = Timeout(5.seconds)


  val config = ConfigFactory.load()

  val ip = config.getString("singlemicro.ip")
  val port = config.getInt("singlemicro.port")

  IO(Http) ? Http.Bind(service, interface = ip, port = port)
  logger.info(s"MicroserviceMain booting ip:$ip port:$port")

  StatusService.signalStartedOrExit()


}
