![scalaVersion](https://img.shields.io/badge/scala-2.12.4-blue.svg) ![license](https://img.shields.io/badge/license-Apache%202-blue.svg)

# factorie-nlp-api

This is a modified version of [https://github.com/factorie/factorie](https://github.com/factorie/factorie) focused on the NLP features via the API. It is opinionated and therefore does not include all of the NLP options contained in the original Factorie code base.

It is also stripped all of the command line code, code for accessing MongoDB, and the docs and examples associated with the general (non NLP) use of Factorie.

The original Factorie was a maven project with a custom sbt builder. This has been replaced it with a standard ```build.sbt```
and the libraries have been updated to allow it to run on Scala 2.12.
