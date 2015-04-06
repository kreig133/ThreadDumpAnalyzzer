// Name of the project
name := "dump analyz"

// Project version
version := "1.0"

// Version of Scala used by the project
scalaVersion := "2.11.6"

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.40-R8"

libraryDependencies += "junit" % "junit" % "4.12" % Test


// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true