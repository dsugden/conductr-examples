import ByteConversions._
import com.typesafe.sbt.bundle.Import.BundleKeys


name := "conductR-examples"

version  := "1.0.0"

lazy val Akka  = "2.3.6"
lazy val Spray = "1.3.1"


lazy val singlemicro = (project in file("singlemicro"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "singlemicro",
    version  := "1.0.0",
    scalaVersion := "2.11.6",
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 64.MiB ,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.endpoints := Map("singlemicro" -> Endpoint("http", 8082, Set(URI("http:/singlemicro")))),
    resolvers += "typesafe-releases" at "http://repo.typesafe.com/typesafe/maven-releases",
    libraryDependencies ++=  Seq(
      "com.typesafe.akka"          %% "akka-actor"      % Akka     % "compile",
      "com.typesafe.akka"          %% "akka-slf4j"      % Akka     % "compile",
      "ch.qos.logback"              % "logback-classic" % "1.1.2"  % "compile",
      "javax.mail"                  % "mail"            % "1.4.5"  % "compile",
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
    ),
    javaOptions ++= Seq(
      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
      "-Xms128m", "-Xmx1024m"),
    // this enables custom javaOptions
    fork in run := true
  )



fork in Test := false

fork in IntegrationTest := false

parallelExecution in Test := false

parallelExecution in IntegrationTest := false

