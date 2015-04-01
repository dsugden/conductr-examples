import ByteConversions._
import com.typesafe.sbt.bundle.Import.BundleKeys
import play.PlayScala


name := "conductR-examples"

version  := "1.0.0"

lazy val scalaV = "2.11.6"

lazy val singlemicro = (project in file("singlemicro"))
  .enablePlugins(JavaAppPackaging,SbtTypesafeConductR)
  .settings(
    name := "singlemicro",
    version  := "1.0.0",
    scalaVersion := scalaV,
    target := file("singlemicro") / "singlemicro",
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 64.MiB ,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.endpoints := Map("singlemicro" -> Endpoint("http", 8095, Set(URI("http:/singlemicro")))),
    resolvers += "typesafe-releases" at "http://repo.typesafe.com/typesafe/maven-releases",
    libraryDependencies ++= Dependencies.singlemicro,
    javaOptions ++= Seq(
      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
      "-Xms128m", "-Xmx1024m"),
    // this enables custom javaOptions
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
  .enablePlugins(JavaAppPackaging,SbtTypesafeConductR)
  .settings(
    name := "akkaclusterFront",
    version  := "1.0.0",
    scalaVersion := scalaV,
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 64.MiB ,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.system := "AkkaConductRExamplesClusterSystem",
    BundleKeys.endpoints := Map(
      "akka-remote" -> Endpoint("tcp", 8083, Set.empty),
      "spray-http" -> Endpoint("http", 8095, Set(URI("http:/test")))),
    resolvers += "typesafe-releases" at "http://repo.typesafe.com/typesafe/maven-releases",
    libraryDependencies ++= Dependencies.akkacluster,
    javaOptions ++= Seq(
      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
      "-Xms128m", "-Xmx1024m"),
    fork in run := true
  ).dependsOn(akkaclusterApi)

lazy val akkaclusterBack = (project in file("akkaclusterback"))
  .enablePlugins(JavaAppPackaging,SbtTypesafeConductR)
  .settings(
    name := "akkaclusterBack",
    version  := "1.0.0",
    scalaVersion := scalaV,
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 64.MiB ,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.system := "AkkaConductRExamplesClusterSystem",
    BundleKeys.endpoints := Map("akka-remote" -> Endpoint("tcp", 8083, Set.empty)),
    resolvers += "typesafe-releases" at "http://repo.typesafe.com/typesafe/maven-releases",
    libraryDependencies ++= Dependencies.akkacluster,
    javaOptions ++= Seq(
      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
      "-Xms128m", "-Xmx1024m"),
    fork in run := true
  ).dependsOn(akkaclusterApi)





lazy val playProject = (project in file("playProject"))
  .enablePlugins(PlayScala,JavaAppPackaging,SbtTypesafeConductR)
  .settings(
    name := "playProject",
    version  := "1.0.0",
    scalaVersion := scalaV,
    libraryDependencies ++= Dependencies.playProject,
    BundleKeys.nrOfCpus := 1.0,
    BundleKeys.memory := 64.MiB ,
    BundleKeys.diskSpace := 5.MB,
    BundleKeys.endpoints := Map("playproject" -> Endpoint("http", 9000, Set(URI("http:/test")))),
    javaOptions ++= Seq("-Xms128m", "-Xmx1024m"),
    fork in run := true
  )






fork in Test := false

fork in IntegrationTest := false

parallelExecution in Test := false

parallelExecution in IntegrationTest := false

