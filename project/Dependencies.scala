import sbt._

object Dependencies {

  val akkaVersion = "2.5.6"
  val akkaHttpVersion = "10.0.10"
  val cassandraDriverVersion = "3.3.1"
  val log4jVersion = "2.9.1"

  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10"
  val akkaStreamsContrib =  "com.typesafe.akka" %% "akka-stream-contrib" % "0.8"
  val log4j2Api = "org.apache.logging.log4j" % "log4j-api" % log4jVersion
  val log4j2Core = "org.apache.logging.log4j" % "log4j-core" % log4jVersion % Runtime
  val log4jSlf4J = "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion
  val typesafeLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

  val cassandraDriver = "com.datastax.cassandra" % "cassandra-driver-core" % cassandraDriverVersion

  val akkaStreamsTestKit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test

  val tcpStreamsDeps: Seq[ModuleID] = Seq(
    akkaStreams, akkaStreamsTestKit, akkaStreamsContrib
  )

  val httpStreamsDeps: Seq[ModuleID] = tcpStreamsDeps ++ Seq(
    akkaHttp, akkaHttpTestKit, akkaStreamsContrib, cassandraDriver, akkaHttpSpray,
    log4j2Api, log4j2Core, log4jSlf4J, typesafeLogging
  )

  val syncDeps: Seq[ModuleID] = Seq()
}

