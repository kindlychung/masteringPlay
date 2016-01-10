name := """TestingApp"""

version := "1.0.0"

scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(jdbc,anorm,"org.scalatestplus" %% "play" % "1.1.0" % "test")

