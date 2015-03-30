package com.boldradius.conductr.examples

import akka.actor.{ActorLogging, Actor}


sealed trait AkkaClusterBackendProtocol
case object Trivial extends  AkkaClusterBackendProtocol

class AkkaClusterBackend extends Actor with ActorLogging with AkkaClusterBackendProtocol{

  def receive = {
    case msg => msg.asInstanceOf[AkkaClusterBackendProtocol] match {
      case Trivial => sender() ! "AkkaClusterBackend success"
    }
  }
}



object AkkaClusterBackend extends App{

}
