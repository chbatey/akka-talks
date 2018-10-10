import sbt._

object Dependencies {

  val akkaVersion = "2.5.17"

  val akkaHttpVersion = "10.1.3"
  val cassandraDriverVersion = "3.3.1"
  val log4jVersion = "2.9.1"
  val gatlingVersion = "2.3.0"
  val akkaGrpcVersion = "0.4.1"
  val playVersion = "2.7.0-M2"
  val akkaManagementVersion = "0.18.0"
  val akkaCommercialVersion = "1.1.2"
  val alpakkaVersion = "1.0-M1"
  val alpakkaKafkaVersion = "0.22"

  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion

  val akkaTyped = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  val akkaClusterTyped = "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion
  val akkaClusterShardingTyped = "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion
  val akkaStreamsTyped = "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion
  val akkaDiscoveryConfig = "com.lightbend.akka.discovery" %% "akka-discovery-config" % akkaManagementVersion
  val akkaSbr = "com.lightbend.akka" %% "akka-split-brain-resolver" % akkaCommercialVersion
  val akkaManagementHttp = "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion
  val alpakkaKafka = "com.typesafe.akka" %% "akka-stream-kafka" % alpakkaKafkaVersion

  val play = "com.typesafe.play" %% "play" % playVersion

  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  val akkaStreamsContrib = "com.typesafe.akka" %% "akka-stream-contrib" % "0.8"
  val log4j2Api = "org.apache.logging.log4j" % "log4j-api" % log4jVersion
  val log4j2Core = "org.apache.logging.log4j" % "log4j-core" % log4jVersion % Runtime
  val log4jSlf4J = "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion
  val typesafeLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

  val alpakkaFtp = "com.lightbend.akka" %% "akka-stream-alpakka-ftp" % alpakkaVersion
  val alpakkaCassandra = "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % alpakkaVersion

  val gatHighCharts = "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % Test
  val gatling = "io.gatling" % "gatling-test-framework" % gatlingVersion % Test

  val cassandraDriver = "com.datastax.cassandra" % "cassandra-driver-core" % cassandraDriverVersion

  val akkaStreamsTestKit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test

  val tcpStreamsDeps = Seq(
    akkaStreams, akkaStreamsTestKit, akkaStreamsContrib
  )

  val httpStreamsDeps = tcpStreamsDeps ++ Seq(
    akkaHttp, akkaHttpTestKit, akkaStreamsContrib, cassandraDriver, akkaHttpSpray,
    log4j2Api, log4j2Core, log4jSlf4J, typesafeLogging, alpakkaFtp, gatHighCharts, gatling, alpakkaCassandra
  )

  val akkaOverviewDeps = Seq(akkaStreams, akkaHttp, akkaTyped, akkaStreamsTyped, akkaClusterTyped)

  val akkaGrpcSampleDeps = Seq(
    akkaStreams, akkaHttp, akkaTyped, akkaStreamsTyped, akkaDiscoveryConfig,
    play, alpakkaKafka, alpakkaCassandra
  )

  val akkaTypedDeps = Seq(akkaTyped)

  val akkaClusterDeps = Seq(akkaTyped, akkaClusterTyped, akkaClusterShardingTyped, akkaSbr, akkaManagementHttp)

  val syncDeps: Seq[ModuleID] = Seq()
}

