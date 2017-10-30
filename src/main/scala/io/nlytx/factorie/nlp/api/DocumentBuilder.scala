package io.nlytx.factorie.nlp.api

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
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

class DocumentBuilder {


  implicit val system: ActorSystem = ActorSystem("factorie-nlp-api-as")
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  //Document Annotators - no models required
  private val tokeniser = (doc: Document) => DeterministicNormalizingTokenizer.process(doc)
  private val segmenter = (doc: Document) => DeterministicSentenceSegmenter.process(doc)
  private val normaliser = (doc: Document) => PlainTokenNormalizer.process(doc)

  //Document Annotators - load models
  private val postagger = (doc: Document) => OntonotesForwardPosTagger.process(doc)
  private lazy val wordNetLemmatizer = wordnet.wnLemmatizer
  private val lemmatiser = (doc: Document) => wordNetLemmatizer.process(doc)
  private val parser = (doc: Document) => OntonotesTransitionBasedParser.process(doc)

  //Very slow model loading - returns a future
  private val nerTagger = (doc: Document) => Future(SlowLoad.nerTagger.process(doc))

  //Pipeline types
  type TokenSegment = Flow[String, Document, NotUsed]
  type Complete = Flow[String, Document, NotUsed]
  type PosTag = Flow[Document, Document, NotUsed]
  type Lemma = Flow[Document, Document, NotUsed]
  type Parse = Flow[Document, Document, NotUsed]
  type NerTag = Flow[Document, Document, NotUsed]

  //Pipeline segments
  private val doc = Flow[String].map(new Document(_))
  private val tokenise = Flow[Document].map(tokeniser).map(segmenter)
  private val posTag = Flow[Document].map(normaliser).map(postagger)
  private val lemma = Flow[Document].map(lemmatiser)
  private val parse = Flow[Document].map(parser)
  private val ner = Flow[Document].mapAsync(2)(nerTagger)

  //Pipelines
  private val tsPipe: TokenSegment = doc via tokenise
  private val tokenSegmentPipeline = (s:String) => Source.single(s).via(tsPipe).toMat(Sink.head[Document])(Keep.right)

  private val ptPipe: PosTag = posTag
  private val posTagPipeline = (d:Document) => Source.single(d).via(ptPipe).toMat(Sink.head[Document])(Keep.right)

  private val lemPipe: Lemma = lemma
  private val lemmaPipeline = (d:Document) => Source.single(d).via(lemPipe).toMat(Sink.head[Document])(Keep.right)

  private val parsePipe: Parse = parse
  private val parsePipeline = (d:Document) => Source.single(d).via(parsePipe).toMat(Sink.head[Document])(Keep.right)

  private val nerPipe: NerTag = ner
  private val nerPipeline = (d:Document) => Source.single(d).via(nerPipe).toMat(Sink.head[Document])(Keep.right)

  private val completePipe: Complete = doc via tokenise via posTag via lemma via parse via ner
  private val completePipeline = (s:String) => Source.single(s).via(completePipe).toMat(Sink.head[Document])(Keep.right)


  def process[T: TypeTag](input: Any): Future[Document] = input match {
    case text: String if typeOf[T] <:< typeOf[Complete] => completePipeline(text).run
    case text: String if typeOf[T] <:< typeOf[TokenSegment] => tokenSegmentPipeline(text).run
    case doc: Document if typeOf[T] <:< typeOf[PosTag] => posTagPipeline(doc).run
    case doc: Document if typeOf[T] <:< typeOf[Lemma] => lemmaPipeline(doc).run
    case doc: Document if typeOf[T] <:< typeOf[Parse] => parsePipeline(doc).run
    case doc: Document if typeOf[T] <:< typeOf[NerTag] => parsePipeline(doc).run
    case _ => {
      println("Unknown format")
      Future(new Document(""))
    }
  }

  def show(fdoc:Future[Document]) = {
    val doc = Await.result(fdoc, 120 second)
    doc.sentences.foreach { s =>
      println(s"Sentence index: ${s.indexInSection}")
      println(s"Sentence parse: ${s.parse.toString}")
      s.tokens.foreach { t =>
        println(s"Token: ${t.toString}")
        println(s"Position: ${t.positionInSentence}")
        println(s"PosTag: ${t.posTag.toString}")
        println(s"Lemma: ${t.lemmaString}")
        if (!t.nerTag.isEmpty) println(s"NerTag: ${t.nerTag.baseCategoryValue}")
      }
    }
  }

  def wordnet:WordNet = {
    val streamFactory = (file:String) => this.getClass.getResourceAsStream("/cc/factorie/app/nlp/wordnet/"+file)
    new WordNet(streamFactory)
  }

}







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