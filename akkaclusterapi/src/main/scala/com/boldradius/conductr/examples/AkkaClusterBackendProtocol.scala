package com.boldradius.conductr.examples

sealed trait AkkaClusterBackendProtocol
case object Trivial extends  AkkaClusterBackendProtocol
case object BackendRegistration extends  AkkaClusterBackendProtocol
