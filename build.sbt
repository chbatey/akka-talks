import Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      // Same as:
      // organization in ThisBuild := "com.example"
      organization := "info.batey.akka",
      name := "backpressure-talk",
      scalaVersion := "2.12.5",
      version := "0.1.0-SNAPSHOT"
    )))
  .settings(
    name := "akka-streams"
  )
  .aggregate(tcpStreams, httpStreams, syncExamples, presentation, presentationParadox, akkaStreams)

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

lazy val presentation = (project in file("presentation"))
  .dependsOn(tcpStreams, httpStreams, syncExamples)
  .settings(
    tutSourceDirectory := baseDirectory.value / "tut",
    tutTargetDirectory := baseDirectory.value / "../docs",
    watchSources ++= (tutSourceDirectory.value ** "*.html").get
  )
  .enablePlugins(TutPlugin)

lazy val presentationParadox = (project in file("presentation-paradox"))
  .dependsOn(tcpStreams, httpStreams, syncExamples)
  .settings(
    paradoxGroups := Map("Language" -> Seq("Scala", "Java")),
    paradoxProperties += ("selectedLanguage" → sys.env.getOrElse("PARADOX_LANGUAGE", "Java"))
  ).enablePlugins(GatlingPlugin).enablePlugins(ParadoxRevealPlugin)


lazy val akkaStreams = (project in file("akka-streams"))
  .settings(
    libraryDependencies ++= tcpStreamsDeps
  )


