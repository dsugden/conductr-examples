import play.PlayImport._
import sbt._

object Dependencies{

  lazy val singlemicroProject = common ++ akka ++ spray ++ Seq(
    "com.typesafe.conductr"      %% "scala-conductr-bundle-lib"  % "0.6.1"
  )


  lazy val akkaclusterProject =   common ++ akka ++ akkacluster ++ spray ++ Seq(
    "com.typesafe.conductr"      % "akka-conductr-bundle-lib_2.11" % "0.6.1"
  )


  lazy val playProject = common ++ Seq(
    ws,
    "com.typesafe.conductr"      % "play-conductr-bundle-lib_2.11" % "0.6.1"
  )



  lazy val AkkaV  = "2.3.6"
  lazy val SprayV = "1.3.3"


  lazy val common = Seq(
    "ch.qos.logback"              % "logback-classic" % "1.1.2"  % "compile",
    "com.typesafe.scala-logging" %% "scala-logging"   % "3.0.0"  % "compile",
    "commons-io"                  % "commons-io"      % "2.4"    % "compile"

  )

  lazy val testing = Seq(
    "org.scalatest"              %% "scalatest"       % "2.2.0"  % "test"
  )

  lazy val akka = Seq(
    "com.typesafe.akka"          %% "akka-actor"      % AkkaV     % "compile",
    "com.typesafe.akka"          %% "akka-slf4j"      % AkkaV     % "compile",
    "com.typesafe.akka"          %% "akka-testkit"    % AkkaV     % "test"
  )

  lazy val akkacluster = Seq(
    "com.typesafe.akka"          %% "akka-cluster"    % AkkaV,
    "com.typesafe.akka"          %% "akka-remote"     % AkkaV,
    "com.typesafe.akka"          %% "akka-contrib"    % AkkaV
  )

  lazy val spray = Seq(
    "io.spray"                   %% "spray-routing"   % SprayV    % "compile",
    "io.spray"                   %% "spray-can"       % SprayV    % "compile",
    "io.spray"                   %% "spray-client"    % SprayV    % "compile",
    "org.json4s"                 %% "json4s-native"   % "3.2.10" % "compile",
    "io.spray"                   %% "spray-testkit"   % SprayV    % "test"
  )


}