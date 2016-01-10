name := """WorkingWithData"""

version := "1.0.0"

scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq("mysql" % "mysql-connector-java" % "5.1.18",
  jdbc,
  anorm,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "com.typesafe.play" %% "play-slick" % "0.8.1"
)

