ThisBuild / scalaVersion     := "2.13.7"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val `search-domain`= project
  .settings(
    libraryDependencies ++= Dependencies.searchServer,
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-language:higherKinds"
    )
  )

lazy val `search-server-protobuf` = project
  .settings(
    libraryDependencies ++= Dependencies.searchServer,
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-language:higherKinds"
    )
  )
  .enablePlugins(Fs2Grpc)

lazy val `search-server` = project
  .dependsOn(`search-domain`, `search-server-protobuf`)
  .settings(
    libraryDependencies ++= Dependencies.searchServer,
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-language:higherKinds"
    )
  )
  .enablePlugins(Fs2Grpc)

lazy val `search-server-grpc` = project
  .dependsOn(`search-domain`, `search-server-protobuf`)
  .settings(
    libraryDependencies ++= Dependencies.searchServer,
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-language:higherKinds"
    )
  )
  .enablePlugins(Fs2Grpc)
