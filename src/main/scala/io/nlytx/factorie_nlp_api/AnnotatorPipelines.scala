package io.nlytx.factorie_nlp_api

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.slf4j.Logger
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.parse.OntonotesTransitionBasedParser
import cc.factorie.app.nlp.pos.OntonotesForwardPosTagger
import cc.factorie.app.nlp.segment.{DeterministicNormalizingTokenizer, DeterministicSentenceSegmenter, PlainTokenNormalizer}
import cc.factorie.app.nlp.wordnet.WordNet

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.reflect.runtime.universe._

/**
  * Created by andrew@andrewresearch.net on 24/10/17.
  */

object AnnotatorPipelines {

  implicit val system: ActorSystem = ActorSystem("factorie-nlp-api-as")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val logger = Logging(system.eventStream, "factorie-nlp-api")

  type Pipeline = String => RunnableGraph[Future[Document]]
  type DocPipeline = Document => RunnableGraph[Future[Document]]

  //Make Document
  private lazy val doc = Flow[String].map(new Document(_))

  //Document Annotators - no models required
  private lazy val tokeniser = (doc: Document) => DeterministicNormalizingTokenizer.process(doc)
  private lazy val segmenter = (doc: Document) => DeterministicSentenceSegmenter.process(doc)
  private lazy val normaliser = (doc: Document) => PlainTokenNormalizer.process(doc)

  //Document Annotators - load models
  private lazy val postagger = (doc: Document) => OntonotesForwardPosTagger.process(doc)
  private lazy val wordNetLemmatizer = wordnet.wnLemmatizer
  private lazy val lemmatiser = (doc: Document) => wordNetLemmatizer.process(doc)
  private lazy val parser = (doc: Document) => OntonotesTransitionBasedParser.process(doc)

  //Very slow model loading - returns a future
  private lazy val nerTagger = (doc: Document) => Future(SlowLoad.nerTagger.process(doc))


  //Pipelines in order of complexity

  val tokenPipeline = (s:String) =>
    Source.single(s)
      .via(doc.map(tokeniser))
      .toMat(Sink.head[Document])(Keep.right)

  val segmentPipeline = (s:String) =>
    Source.single(s)
      .via(doc.map(tokeniser).map(segmenter))
      .toMat(Sink.head[Document])(Keep.right)

  val postagPipeline = (s:String) =>
    Source.single(s)
      .via(doc.map(tokeniser).map(segmenter).map(normaliser).map(postagger))
      .toMat(Sink.head[Document])(Keep.right)

  val fastPipeline = postagPipeline

  val lemmaPipeline = (s:String) =>
    Source.single(s)
      .via(doc.map(tokeniser).map(segmenter).map(normaliser).map(postagger).map(lemmatiser))
      .toMat(Sink.head[Document])(Keep.right)

  val defaultPipeline = lemmaPipeline

  val parsePipeline = (d:Document) =>
    Source.single(d)
      .map(parser)
      .toMat(Sink.head[Document])(Keep.right)

  val nerPipeline = (d:Document) =>
    Source.single(d)
      .mapAsync(2)(nerTagger)
      .toMat(Sink.head[Document])(Keep.right)

  val completePipeline = nerPipeline

  /* The main method for running a pipeline */
  def process(text:String,pipeline:Pipeline=defaultPipeline):Future[Document] = pipeline(text).run

  def processDoc(doc:Document,pipeline:DocPipeline):Future[Document] = pipeline(doc).run

  def profile(text:String,pipeline:Pipeline=defaultPipeline,wait:Int=180):Document = {
    logger.info(s"Profiling pipeline...")
    val start = System.currentTimeMillis()
    val doc = Await.result(process(text,pipeline), wait seconds)
    val time = System.currentTimeMillis() - start
    logger.info(s"Completed in ${time} ms")
    doc
  }

  def wordnet:WordNet = {
    val streamFactory = (file:String) => this.getClass.getResourceAsStream("/cc/factorie/app/nlp/wordnet/"+file)
    new WordNet(streamFactory)
  }

}


//def show(fdoc:Future[Document]) = {
//  val doc = Await.result(fdoc, 120 second)
//  doc.sentences.foreach { s =>
//  println(s"Sentence index: ${s.indexInSection}")
//  println(s"Sentence parse: ${s.parse.toString}")
//  s.tokens.foreach { t =>
//  println(s"Token: ${t.toString}")
//  println(s"Position: ${t.positionInSentence}")
//  println(s"PosTag: ${t.posTag.toString}")
//  println(s"Lemma: ${t.lemmaString}")
//  if (!t.nerTag.isEmpty) println(s"NerTag: ${t.nerTag.baseCategoryValue}")
//}
//}
//}




// Example usages:
// token.sentence.attr[ParseTree].parent(token)
// sentence.attr[ParseTree].children(token)
// sentence.attr[ParseTree].setParent(token, parentToken)
// sentence.attr[ParseTree].label(token)
// sentence.attr[ParseTree].label(token).set("SUBJ")

// Methods also created in Token supporting:
// token.parseParent
// token.setParseParent(parentToken)
// token.parseChildren
// token.parseLabel
// token.leftChildren