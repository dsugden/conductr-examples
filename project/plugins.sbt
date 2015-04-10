logLevel := Level.Warn


resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.bintrayRepo("akka-contrib-extra", "maven")

resolvers += "typesafe-releases" at "http://repo.typesafe.com/typesafe/maven-releases"

//resolvers += "Local ivy Repository" at "file://" + Path.userHome.absolutePath + "/.ivy2/local/"


//addSbtPlugin("com.typesafe.conductr" % "sbt-typesafe-conductr" % "0.28.0-SNAPSHOT","0.13","2.10")
addSbtPlugin("com.typesafe.conductr" % "sbt-typesafe-conductr" % "0.25.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.8")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")




