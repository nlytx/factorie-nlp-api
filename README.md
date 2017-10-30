[![Build Status](https://travis-ci.org/nlytx/factorie-nlp-api.svg?branch=master)](https://travis-ci.org/nlytx/factorie-nlp-api) ![scalaVersion](https://img.shields.io/badge/scala-2.12.4-blue.svg) ![license](https://img.shields.io/badge/license-Apache%202-blue.svg)  [ ![Download](https://api.bintray.com/packages/nlytx/factorie-nlp-api/factorie-nlp-api/images/download.svg?version=0.2) ](https://dl.bintray.com/nlytx-io/factorie-nlp-api/0.3/link)

# factorie-nlp-api

This is a modified version of [https://github.com/factorie/factorie](https://github.com/factorie/factorie) focused on the NLP features via the API. It is opinionated and therefore does not include all of the NLP options contained in the original Factorie code base. In particular, the code for training new models and testing of models has been removed with the objective that this API be focused on delivering NLP services based on existing modles. Therefore, it's anticipated that model training and testing might be provided via a separate API, or with the original Factorie code.

It is also stripped all of the command line code, code for accessing MongoDB, and the docs and examples associated with the general (non NLP) use of Factorie.

The original Factorie was a maven project with a custom sbt builder. This has been replaced it with a standard ```build.sbt```
and the libraries have been updated to allow it to run on Scala 2.12.

You can use this API by including the following in your SBT build file:

```scala
libraryDependencies ++= Seq(
                          "io.nlytx" %% "factorie-nlp-api" % "0.3",
                          "cc.factorie.app.nlp" % "all-models" % "1.2"
                          )

resolvers += Resolver.bintrayRepo("nlytx-io", "factorie-nlp-api")
```
