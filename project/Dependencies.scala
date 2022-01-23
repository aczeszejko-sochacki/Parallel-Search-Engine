import sbt._

object Dependencies {
  object Versions {
    val ce3        = "3.3.0"
    val fs2        = "3.2.4"
    val http4s     = "0.23.6"
    val circe      = "0.14.1"
    val logback    = "1.2.10"
    val log4Cats   = "2.1.1"
    val pureconfig = "0.17.1"
    val fs2Grpc    = "2.4.1"
  }

  val catsEffect3: Seq[ModuleID] =
    Seq(
      "org.typelevel" %% "cats-effect" % Versions.ce3
    )

  val fs2: Seq[ModuleID] =
    Seq(
      "co.fs2"        %% "fs2-core"         % Versions.fs2,
      "co.fs2"        %% "fs2-io"           % Versions.fs2,
      "org.typelevel" %% "fs2-grpc-runtime" % Versions.fs2Grpc
    )

  val http4s: Seq[ModuleID] =
    Seq(
      "org.http4s" %% "http4s-blaze-client",
      "org.http4s" %% "http4s-blaze-server",
      "org.http4s" %% "http4s-dsl",
      "org.http4s" %% "http4s-circe",
    ).map(_ % Versions.http4s)

  val circe: Seq[ModuleID] =
    Seq(
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-literal",
      "io.circe" %% "circe-parser"
    ).map(_ % Versions.circe)

  val logging: Seq[ModuleID] =
    Seq(
      "ch.qos.logback" % "logback-classic" % Versions.logback,
      "org.typelevel" %% "log4cats-slf4j"  % Versions.log4Cats,
    )

  val pureconfig: Seq[ModuleID] =
    Seq(
      "com.github.pureconfig" %% "pureconfig" % Versions.pureconfig
    )

  val grpcNetty: Seq[ModuleID] =
    Seq(
      "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion
    )

  val searchServer: Seq[ModuleID] =
    catsEffect3 ++
      fs2 ++
      http4s ++
      circe ++
      logging ++
      pureconfig ++
      grpcNetty
}
