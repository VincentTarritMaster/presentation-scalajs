ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .enablePlugins(org.scalajs.sbtplugin.ScalaJSPlugin)
  .settings(
    name := "scalajs-reveal-demo",
    version := "0.1.0",
    scalaJSUseMainModuleInitializer := true,
    Compile / mainClass := Some("demo.Main"),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.8.0"
    )
  )
