name := "nlpfactorie"

version := "0.1"

scalaVersion := "2.12.3"

organization := "cc.factorie"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
  "org.scala-lang" % "scala-compiler" % "2.12.3",
  "org.scala-lang" % "scala-reflect" % "2.12.3",
  "org.mongodb" % "mongo-java-driver" % "3.5.0",
"org.jblas" % "jblas" % "1.2.4",
"junit" % "junit" % "4.12",
"org.apache.commons" % "commons-compress" % "1.14",
"commons-lang" % "commons-lang" % "2.6",
"org.xerial.snappy" % "snappy-java" % "1.1.4",
"info.bliki.wiki" % "bliki-core" % "3.1.0",
"org.json4s" %% "json4s-jackson" % "3.5.3",
"com.google.guava" % "guava" % "23.0",
"org.scalatest" %% "scalatest" % "3.0.4" % Test,
"org.slf4j" % "slf4j-log4j12" % "1.7.25" % Test,
"com.github.fakemongo" % "fongo" % "2.1.0" % Test
)

sourceGenerators in Compile <+= sourceManaged in compile map { dir =>
  Seq(dir / "EnglishLexer.scala")
}

//Enable this only for local builds - disabled for Travis
enablePlugins(JavaAppPackaging) // sbt universal:packageZipTarball
//dockerExposedPorts := Seq(9000) // sbt docker:publishLocal