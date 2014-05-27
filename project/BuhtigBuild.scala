import sbt._
import sbt.Keys._

object BuhtigBuild extends Build {

  lazy val buhtig = Project(
    id = "buhtig",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "buhtig",
      organization := "net.caoticode",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.11.1",
        
      // deps
      libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.1"
    )
  )
}
