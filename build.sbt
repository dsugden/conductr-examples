import ByteConversions._
import com.typesafe.sbt.bundle.Import.BundleKeys


name := "conductR-examples"

version  := "1.0.0"

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
    libraryDependencies ++= Dependencies.singlemicro,
    javaOptions ++= Seq(
      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
      "-Xms128m", "-Xmx1024m"),
    // this enables custom javaOptions
    fork in run := true
  )


//lazy val akkacluster = (project in file("akkacluster"))
//  .enablePlugins(JavaAppPackaging)
//  .settings(
//    name := "akkacluster",
//    version  := "1.0.0",
//    scalaVersion := "2.11.6",
//    BundleKeys.nrOfCpus := 1.0,
//    BundleKeys.memory := 64.MiB ,
//    BundleKeys.diskSpace := 5.MB,
//    BundleKeys.endpoints := Map("akkaclusterfront" -> Endpoint("http", 8083, Set(URI("http:/singlemicro")))),
//    resolvers += "typesafe-releases" at "http://repo.typesafe.com/typesafe/maven-releases",
//    libraryDependencies ++= Dependencies.singlemicro,
//    javaOptions ++= Seq(
//      "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath,
//      "-Xms128m", "-Xmx1024m"),
//    // this enables custom javaOptions
//    fork in run := true
//  )
//



fork in Test := false

fork in IntegrationTest := false

parallelExecution in Test := false

parallelExecution in IntegrationTest := false

