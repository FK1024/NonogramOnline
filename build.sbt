enablePlugins(ScalaJSPlugin)
name := "NonogramOnline"
version := "0.1"
scalaVersion := "2.13.4"
scalaJSUseMainModuleInitializer := true
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0"
jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()