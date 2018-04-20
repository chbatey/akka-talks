import Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "info.batey",
      name := "akka-talks",
      scalaVersion := "2.12.5",
      version := "0.1.0-SNAPSHOT"
    )))
  .aggregate(tcpStreams, httpStreams, syncExamples, akkaStreams, akkaOverview,
    presentationFlowControlTut,
    presentationFlowControl,
    presentationAkkaState)

lazy val akkaTypedExamples = (project in file("akka-typed-sample"))
  .settings(
    libraryDependencies ++= akkaTypedDeps
  )

lazy val akkaClusterExample = (project in file("akka-cluster-sample"))
  .settings(
    libraryDependencies ++= akkaClusterDeps
  )


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
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.7" % "runtime"
  )
  .enablePlugins(AkkaGrpcPlugin)
  .enablePlugins(JavaAgent)

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
    paradoxProperties += ("selectedLanguage" → sys.env.getOrElse("PARADOX_LANGUAGE", "Scala"))
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
  .dependsOn(akkaOverview, akkaTypedExamples, akkaClusterExample)

//TODO port to paraodx
lazy val presentationAkkaTyped = (project in file("presentation-akka-typed"))
  .settings(
    paradoxGroups := Map("Language" -> Seq("Scala", "Java")),
    paradoxProperties += ("selectedLanguage" → sys.env.getOrElse("PARADOX_LANGUAGE", "Scala"))
  )
  .enablePlugins(ParadoxRevealPlugin)



