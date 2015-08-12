sonatypeProfileName := "net.caoticode"

useGpg := true

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:mdread/buhtig.git</url>
    <connection>scm:git:git@github.com:mdread/buhtig.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mdread</id>
      <name>Daniel Camarda</name>
      <url>https://github.com/mdread</url>
    </developer>
  </developers>)
