logLevel := Level.Warn


resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.bintrayRepo("akka-contrib-extra", "maven")

addSbtPlugin("com.typesafe.conductr" % "sbt-typesafe-conductr" % "0.25.0")

//addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.0-RC1")
//addSbtPlugin("com.typesafe.sbt" % "sbt-bundle" % "0.18.0")

