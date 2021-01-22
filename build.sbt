enablePlugins(ScalaJSPlugin)
name := "NonogramOnline"
version := "0.1"
scalaVersion := "2.12.7"
scalaJSUseMainModuleInitializer := true
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0"
jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()