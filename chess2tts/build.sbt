name := "chess2tts"

version := "1.0"

scalaVersion := "3.4.2"

lazy val pekkoVersion = "1.0.2"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.13",
  "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test,
  "org.scalanlp" %% "breeze" % "2.1.0"
)
