package cc.factorie.app.nlp.phrase

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.coref.MentionPhraseFinder
import cc.factorie.app.nlp.pos.{PennPosDomain, PennPosTag}

import scala.collection.mutable

/** Apply returns a list of NNP-indicated proper noun phrases, given PennPosTags.
  *
  * @author Andrew McCallum */
object NnpPosNounPhraseFinder extends MentionPhraseFinder {
  def prereqAttrs = Seq(classOf[PennPosTag])
  def apply(doc:Document): Seq[Phrase] = {
    val result = new mutable.ArrayBuffer[Phrase]
    var start = 0
    for (section <- doc.sections) {
      val tokens = section.tokens
      while (start < tokens.length) {
        val token = tokens(start)
        var end = start
        while (end < tokens.length && tokens(end).posTag.intValue == PennPosDomain.nnpIndex) end += 1
        if (end != start && tokens(end-1).posTag.intValue == PennPosDomain.nnpIndex) {
          val phrase = new Phrase(section, token.positionInSection, length=end-start,offsetToHeadToken = -1)
          phrase.attr += new NounPhraseType(phrase, "NAM")
          NounPhraseEntityTypeLabeler.process(phrase)
        }
        start = math.max(start+1, end)
      }
    }
    result
  }
}