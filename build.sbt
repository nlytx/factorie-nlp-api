name := "factorie-nlp-api"

version := "0.2"

scalaVersion := "2.12.4"

organization := "io.nlytx"

val scalaLangV = "2.12.4"
val scalaParserV = "1.0.6"
val jblasV = "1.2.4"
val apacheComsCompressV = "1.15"
val apacheComsLangV = "3.6"

val scalaLangDeps = Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % scalaParserV,
  "org.scala-lang" % "scala-compiler" % scalaLangV,
  "org.scala-lang" % "scala-reflect" % scalaLangV
)

val scalaDeps = Seq(
  "org.json4s" %% "json4s-jackson" % "3.5.3"
)

val javaDeps = Seq(
  "org.jblas" % "jblas" % jblasV,
  "org.apache.commons" % "commons-compress" % apacheComsCompressV,
  "org.apache.commons" % "commons-lang3" % apacheComsLangV
)

libraryDependencies ++= (scalaLangDeps ++ scalaDeps ++ javaDeps)

  //"org.mongodb" % "mongo-java-driver" % "3.5.0",

  //"junit" % "junit" % "4.12",


  //"org.xerial.snappy" % "snappy-java" % "1.1.4",
  //"info.bliki.wiki" % "bliki-core" % "3.1.0",

  //"com.google.guava" % "guava" % "23.0",
  //"org.scalatest" %% "scalatest" % "3.0.4" % Test,
  //"org.slf4j" % "slf4j-log4j12" % "1.7.25" % Test,
  //"com.github.fakemongo" % "fongo" % "2.1.0" % Test


sourceGenerators in Compile += {
  sourceManaged in compile map(dir => Seq(dir / "EnglishLexer.scala"))
}

//Enable this only for local builds - disabled for Travis
enablePlugins(JavaAppPackaging) // sbt universal:packageZipTarball
//dockerExposedPorts := Seq(9000) // sbt docker:publishLocal