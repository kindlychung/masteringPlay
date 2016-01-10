import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object TaskTrackerBuild extends Build {

  val appName = "TaskTracker"
  val appVersion = "1.0"

  val scalaVersion = "2.10.4"

  val appDependencies = Seq(
    // Add your project dependencies here
  )

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    libraryDependencies ++= appDependencies
  )
}
