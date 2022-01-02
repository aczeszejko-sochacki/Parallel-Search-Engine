ThisBuild / scalaVersion     := "2.13.7"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val `search-server` = project
  .settings(
    libraryDependencies ++= Dependencies.searchServer,
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-language:higherKinds",
    )
  )
