ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "work.arudenko"
ThisBuild / scalaVersion := "3.2.2"

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

val tikaVersion =  "2.6.0"
val scalaLikeJdbcVersion = "4.0.0"
val scalatestVersion = "3.2.15"

lazy val root = (project in file("."))
  .settings(
    name := "docmanage",
    libraryDependencies ++= Seq(
      "org.apache.lucene" % "lucene-core" % "9.4.2",
      "org.apache.tika" % "tika-core" % tikaVersion,
      "org.apache.tika" % "tika-parsers" % tikaVersion,
      "org.apache.derby" % "derby" % "10.16.1.1",
      "com.typesafe" % "config" % "1.4.2",
      "ch.qos.logback" % "logback-classic" % "1.4.5",
      "org.scalikejdbc" %% "scalikejdbc" % scalaLikeJdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-joda-time" % scalaLikeJdbcVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "org.scalactic" %% "scalactic" % scalatestVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion % "test",
      "com.google.guava" % "guava" % "31.1-jre"
    )
  )
