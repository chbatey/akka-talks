addSbtPlugin("org.tpolecat" % "tut-plugin" % "0.6.4")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.3.3")
addSbtPlugin("net.bzzt" % "sbt-paradox-reveal-js" % "0.4")
addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.0-M2")

// gRPC without Akka
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.18")

// gRPC Akka
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.4")
addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "0.4.1")
