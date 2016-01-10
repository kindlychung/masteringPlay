import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object HelloWorldBuild extends Build {

  val appName = "HelloWorld"
  val appVersion = "1.0.0"

  val scala = "2.10.4"

  val appSettings = Seq(
    scalaVersion := scala,
    version := appVersion,
    routesImport += "customUtils.CustomBinders._"
  )

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    appSettings: _*
  )
}
