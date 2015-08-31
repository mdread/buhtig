import sbt._
import sbt.Keys._

object BuhtigBuild extends Build {

  lazy val buhtig = Project(
    id = "buhtig",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "buhtig",
      organization := "net.caoticode.buhtig",
      version := "0.2.0",
      scalaVersion := "2.11.7",
      crossScalaVersions := Seq("2.10.5", "2.11.7"),
      homepage      := Some(url("https://github.com/mdread/buhtig")),
      description   := "Painless Github client",
      licenses      := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),

      // deps
      libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",

      // testing deps
      libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.6.4" % "test"),
      scalacOptions in Test ++= Seq("-Yrangepos")
    )
  )
}
