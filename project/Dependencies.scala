import sbt._

object Dependencies {

  val akkaVersion = "2.5.11"
  val akkaHttpVersion = "10.1.0"
  val cassandraDriverVersion = "3.3.1"
  val log4jVersion = "2.9.1"
  val gatlingVersion = "2.3.0"

  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10"
  val akkaStreamsContrib = "com.typesafe.akka" %% "akka-stream-contrib" % "0.8"
  val log4j2Api = "org.apache.logging.log4j" % "log4j-api" % log4jVersion
  val log4j2Core = "org.apache.logging.log4j" % "log4j-core" % log4jVersion % Runtime
  val log4jSlf4J = "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion
  val typesafeLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

  val alpakkaFtp = "com.lightbend.akka" %% "akka-stream-alpakka-ftp" % "0.16"
  val alpakkaCassandra = "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % "0.18"

  val gatHighCharts = "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % Test
  val gatling = "io.gatling" % "gatling-test-framework" % gatlingVersion % Test

  val cassandraDriver = "com.datastax.cassandra" % "cassandra-driver-core" % cassandraDriverVersion

  val akkaStreamsTestKit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test

  val tcpStreamsDeps: Seq[ModuleID] = Seq(
    akkaStreams, akkaStreamsTestKit, akkaStreamsContrib
  )

  val httpStreamsDeps: Seq[ModuleID] = tcpStreamsDeps ++ Seq(
    akkaHttp, akkaHttpTestKit, akkaStreamsContrib, cassandraDriver, akkaHttpSpray,
    log4j2Api, log4j2Core, log4jSlf4J, typesafeLogging, alpakkaFtp, gatHighCharts, gatling, alpakkaCassandra
  )

  val syncDeps: Seq[ModuleID] = Seq()
}

