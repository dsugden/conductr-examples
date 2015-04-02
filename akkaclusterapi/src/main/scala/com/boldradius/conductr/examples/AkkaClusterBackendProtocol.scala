package com.boldradius.conductr.examples

sealed trait AkkaClusterBackendProtocol
case class Job(name:String) extends  AkkaClusterBackendProtocol
case object BackendRegistration extends  AkkaClusterBackendProtocol
