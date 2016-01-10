name := """TestingAppWithGuice"""

version := "1.0.0"

scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(jdbc,
    anorm,
    "com.google.inject" % "guice" % "3.0",
    "javax.inject" % "javax.inject" % "1",
    "org.scalatestplus" %% "play" % "1.1.0" % "test"
)

