import ByteConversions._
import com.typesafe.sbt.bundle.Import.BundleKeys
import play.PlayScala



name := "conductR-examples"

version  := "1.0.0"

lazy val scalaV = "2.11.7"

lazy val micro = (project in file("micro"))
  .enablePlugins(JavaAppPackaging,ConductRPlugin)
  .settings(
    name := "micro",
    version  := "1.0.0",
    scalaVersion := scalaV,
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 64.MiB ,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.roles  := Set("backend"),
    BundleKeys.endpoints := Map("micro" -> Endpoint("http",0,services = Set(URI("http:/micro")))),
    libraryDependencies ++= Dependencies.microProject,
    javaOptions ++= Seq(
      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
      "-Xms128m", "-Xmx512m"),
    fork in run := true
  )


lazy val akkaclusterApi = (project in file("akkaclusterapi"))
  .settings(
    name := "akkaclusterapi",
    version  := "1.0.0",
    scalaVersion := scalaV,
    resolvers += "typesafe-releases" at "http://repo.typesafe.com/typesafe/maven-releases",
    libraryDependencies ++= Dependencies.akkacluster
  )


lazy val akkaclusterFront = (project in file("akkaclusterfront"))
  .enablePlugins(JavaAppPackaging,ConductRPlugin)
  .settings(
    name := "akkaclusterFront",
    version  := "1.0.0",
    scalaVersion := scalaV,
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 1024.MiB,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.system := "AkkaConductRExamplesClusterSystem",
    BundleKeys.roles  := Set("frontend"),
    BundleKeys.endpoints := Map(
      "akka-remote" -> Endpoint("tcp", 8083, Set.empty),
      "spray-http" -> Endpoint("http", 8095, Set(URI("http:/spray-http")))),
    libraryDependencies ++= Dependencies.akkaclusterProject,
    javaOptions ++= Seq(
      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
      "-Xms128m", "-Xmx512m"),
    fork in run := true
  ).dependsOn(akkaclusterApi)

lazy val akkaclusterBack = (project in file("akkaclusterback"))
  .enablePlugins(JavaAppPackaging,ConductRPlugin)
  .settings(
    name := "akkaclusterBack",
    version  := "1.0.0",
    scalaVersion := scalaV,
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 1024.MiB,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.roles  := Set("backend"),
    BundleKeys.system := "AkkaConductRExamplesClusterSystem",
    BundleKeys.endpoints := Map("akka-remote" -> Endpoint("tcp", 8084, Set.empty)),
    libraryDependencies ++= Dependencies.akkaclusterProject,
    javaOptions ++= Seq(
      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
      "-Xms128m", "-Xmx512m"),
    fork in run := true
  ).dependsOn(akkaclusterApi)


lazy val playProject = (project in file("playProject"))
  .enablePlugins(PlayScala,JavaAppPackaging,ConductRPlugin)
  .settings(
    name := "playProject",
    version  := "1.0.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Dependencies.playProject,
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 64.MiB ,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.roles  := Set("frontend"),
    BundleKeys.endpoints := Map("playproject" -> Endpoint("http", 9000, Set(URI("http:/test")))),
    javaOptions ++= Seq("-Xms128m", "-Xmx512m"),
    fork in run := true
  )


fork in Test := false

fork in IntegrationTest := false

parallelExecution in Test := false

parallelExecution in IntegrationTest := false

