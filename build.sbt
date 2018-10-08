import Dependencies._
import akka.grpc.gen.scaladsl.play._
import scalapb.options.Scalapb

lazy val akkaTalks = (project in file("."))
  .settings(
    name := "akka-talks",
    inThisBuild(List(
      organization := "info.batey",
      name := "akka-talks",
      scalaVersion := "2.12.6",
      version := "0.1.0-SNAPSHOT",
      resolvers += "akka snapshots" at "https://repo.akka.io/snapshots/",
      resolvers += "com-mvn" at "https://repo.lightbend.com/commercial-releases/"
    )))
  .aggregate(tcpStreams, httpStreams, syncExamples, akkaStreams, akkaOverview,
    presentationFlowControlTut,
    presentationFlowControl,
    presentationAkkaState)


lazy val playExamples = (project in file("play-sample"))
  .enablePlugins(PlayScala, PlayAkkaHttp2Support)
  .settings(
    libraryDependencies += guice
  )
  .dependsOn(akkaGrpcSample)


lazy val akkaTypedExamples = (project in file("akka-typed-sample"))
  .settings(
    libraryDependencies ++= akkaTypedDeps
  )

lazy val akkaClusterExample = (project in file("akka-cluster-sample"))
  .settings(
    libraryDependencies ++= akkaClusterDeps
  )
  .enablePlugins(MultiJvmPlugin)


lazy val tcpStreams = (project in file("tcp-streams"))
  .settings(
    libraryDependencies ++= tcpStreamsDeps
  )

lazy val httpStreams = (project in file("http-streams"))
  .settings(
    libraryDependencies ++= httpStreamsDeps
  )

lazy val syncExamples = (project in file("sync-examples"))
  .settings(
    libraryDependencies ++= syncDeps
  )
  .dependsOn(httpStreams) //steal the domain classes

lazy val akkaStreams = (project in file("akka-streams"))
  .settings(
    libraryDependencies ++= tcpStreamsDeps
  )

lazy val akkaOverview = (project in file("akka-overview"))
  .settings(
    libraryDependencies ++= akkaOverviewDeps
  )

lazy val akkaGrpcSample = (project in file("akka-grpc-sample"))
  .settings(
    libraryDependencies ++= akkaGrpcSampleDeps,
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.7" % "runtime",
    akkaGrpcExtraGenerators += PlayScalaClientCodeGenerator,
    akkaGrpcExtraGenerators += PlayScalaServerCodeGenerator
  )
  .dependsOn(httpStreams)
  .enablePlugins(AkkaGrpcPlugin)
  .enablePlugins(JavaAgent)

lazy val grpcJavaSample = (project in file("grpc-java"))
  .settings(
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    ),
    libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.8.1",
    libraryDependencies ++= Seq("com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
      "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion)
  )

lazy val presentationFlowControlTut = (project in file("presentation-flow-control-tut"))
  .dependsOn(tcpStreams, httpStreams, syncExamples)
  .settings(
    tutSourceDirectory := baseDirectory.value / "tut",
    tutTargetDirectory := baseDirectory.value / "../docs",
    watchSources ++= (tutSourceDirectory.value ** "*.html").get
  )
  .enablePlugins(TutPlugin)

lazy val presentationFlowControl = (project in file("presentation-flow-control"))
  .dependsOn(tcpStreams, httpStreams, syncExamples)
  .settings(
    paradoxGroups := Map("Language" -> Seq("Scala", "Java")),
    paradoxProperties += ("selectedLanguage" → sys.env.getOrElse("PARADOX_LANGUAGE", "Java"))
  )
  .enablePlugins(GatlingPlugin)
  .enablePlugins(ParadoxRevealPlugin)
  .enablePlugins(ParadoxPlugin)

lazy val presentationAkkaState = (project in file("presentation-akka-state"))
  .settings(
    paradoxGroups := Map("Language" -> Seq("Scala", "Java")),
    paradoxProperties += ("selectedLanguage" → sys.env.getOrElse("PARADOX_LANGUAGE", "Scala"))
  )
  .enablePlugins(ParadoxRevealPlugin)
  .enablePlugins(ParadoxPlugin)
  .dependsOn(akkaOverview, akkaTypedExamples, akkaClusterExample, akkaGrpcSample)

//TODO port to paraodx
lazy val presentationAkkaTyped = (project in file("presentation-akka-typed"))
  .settings(
    paradoxGroups := Map("Language" -> Seq("Scala", "Java")),
    paradoxProperties += ("selectedLanguage" → sys.env.getOrElse("PARADOX_LANGUAGE", "Scala"))
  )
  .enablePlugins(ParadoxRevealPlugin)



