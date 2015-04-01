import play.PlayImport._
import sbt._

object Dependencies{


  lazy val Akka  = "2.3.6"
  lazy val Spray = "1.3.1"


  lazy val singlemicro = Seq(
    "com.typesafe.akka"          %% "akka-actor"      % Akka     % "compile",
    "com.typesafe.akka"          %% "akka-slf4j"      % Akka     % "compile",
    "ch.qos.logback"              % "logback-classic" % "1.1.2"  % "compile",
    "io.spray"                   %% "spray-routing"   % Spray    % "compile",
    "io.spray"                   %% "spray-can"       % Spray    % "compile",
    "io.spray"                   %% "spray-client"    % Spray    % "compile",
    "org.json4s"                 %% "json4s-native"   % "3.2.10" % "compile",
    "com.typesafe.scala-logging" %% "scala-logging"   % "3.0.0"  % "compile",
    "commons-io"                  % "commons-io"      % "2.4"    % "compile",
    "org.scalatest"              %% "scalatest"       % "2.2.0"  % "test",
    "com.typesafe.akka"          %% "akka-testkit"    % Akka     % "test",
    "io.spray"                   %% "spray-testkit"   % Spray    % "test",
    "com.typesafe.conductr"      %% "scala-conductr-bundle-lib"  % "0.6.1"

  )


  lazy val akkacluster =  Seq(
    "com.typesafe.akka"          %% "akka-actor"      % Akka     % "compile",
    "com.typesafe.akka"          %% "akka-slf4j"      % Akka     % "compile",
    "com.typesafe.akka"          %% "akka-cluster"                           % Akka,
    "com.typesafe.akka"          %% "akka-remote"                            % Akka,
    "com.typesafe.akka"          %% "akka-contrib"                           % Akka,
    "ch.qos.logback"              % "logback-classic" % "1.1.2"  % "compile",
    "io.spray"                   %% "spray-routing"   % Spray    % "compile",
    "io.spray"                   %% "spray-can"       % Spray    % "compile",
    "io.spray"                   %% "spray-client"    % Spray    % "compile",
    "org.json4s"                 %% "json4s-native"   % "3.2.10" % "compile",
    "com.typesafe.scala-logging" %% "scala-logging"   % "3.0.0"  % "compile",
    "commons-io"                  % "commons-io"      % "2.4"    % "compile",
    "org.scalatest"              %% "scalatest"       % "2.2.0"  % "test",
    "com.typesafe.akka"          %% "akka-testkit"    % Akka     % "test",
    "io.spray"                   %% "spray-testkit"   % Spray    % "test",
    "com.typesafe.conductr"      % "akka-conductr-bundle-lib_2.11" % "0.6.1"
  )


  lazy val playProject = Seq(ws)

}