package cc.factorie.app.nlp.phrase

import cc.factorie.app.nlp.morph.BasicMorphologicalAnalyzer
import cc.factorie.app.nlp.pos.PennPosTag
import cc.factorie.app.nlp.{Document, DocumentAnnotator, Token}

import scala.reflect.ClassTag

/** Cheap number predictor based on rules and lexicon.  Really this should use a real morphological analyzer. */
class NounPhraseNumberLabeler[A<:AnyRef](documentAttrToPhrases:(A)=>Iterable[Phrase])(implicit docAttrClass:ClassTag[A]) extends DocumentAnnotator {
  val singularPronoun = Set("i", "me", "my", "mine", "myself", "he", "she", "it", "him", "her", "his", "hers", "its", "one", "ones", "oneself", "this", "that")
  val pluralPronoun = Set("we", "us", "our", "ours", "ourselves", "ourself", "they", "them", "their", "theirs", "themselves", "themself", "these", "those")
  val singularDeterminer = Set("a", "an", "this")
  val pluralDeterminer = Set("those", "these", "some", "both")
  def isProper(pos:String): Boolean = pos.startsWith("NNP")
  def isNoun(pos:String): Boolean = pos(0) == 'N'
  def isPossessive(pos:String): Boolean = pos == "POS"
  def process(document:Document): Document = {
    for (phrase <- documentAttrToPhrases(document.attr[A])) process(phrase)
    document
  }
  def process(phrase:Phrase): Unit = {
    import NumberDomain._
    val number = new PhraseNumber(phrase, UNKNOWN)
    phrase.attr += number
    if (phrase.length > 0) {
      val firstWord = phrase(0).string.toLowerCase
      val headPos = phrase.headToken.attr[PennPosTag].categoryValue
      if (singularPronoun.contains(firstWord) || singularDeterminer.contains(firstWord)) number := SINGULAR
      else if (pluralPronoun.contains(firstWord) || pluralDeterminer.contains(firstWord)) number := PLURAL
      else if (isProper(headPos) && phrase.exists(token => token.string.toLowerCase == "and")) number := PLURAL
      else if (isNoun(headPos) || isPossessive(headPos)) {
        val headWord = phrase.headToken.string.toLowerCase
        if (BasicMorphologicalAnalyzer.isPlural(headWord)) number := PLURAL
        else if (headPos.startsWith("N")) { if (headPos.endsWith("S")) number := PLURAL else number := SINGULAR }
        else number := SINGULAR
      }
    }
  }
  override def tokenAnnotationString(token:Token): String = { val phrases = documentAttrToPhrases(token.document.attr[A]).filter(_.contains(token)); phrases.map(_.attr[Number].categoryValue).mkString(",") }
  override def phraseAnnotationString(phrase:Phrase): String = { val t = phrase.attr[Number]; if (t ne null) t.categoryValue else "_" }
  def prereqAttrs: Iterable[Class[_]] = List(classOf[PennPosTag], classOf[NounPhraseList])
  def postAttrs: Iterable[Class[_]] = List(classOf[PhraseNumber])
}

//class NounPhraseNumberLabeler extends PhraseNumberLabeler[NounPhraseList](phrases => phrases)
object NounPhraseNumberLabeler extends NounPhraseNumberLabeler[NounPhraseList](phrases => phrases)
