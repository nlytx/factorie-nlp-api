package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.phrase.{NounPhraseType, Phrase}
import cc.factorie.app.nlp.pos.PennPosTag

/** Apply returns a list of pronoun phrases, given PennPosTags.
  *
  * @author Andrew McCallum */
object PronounFinder extends MentionPhraseFinder {
  def prereqAttrs = Seq(classOf[PennPosTag])
  def apply(document:Document): Seq[Phrase] = {
    val phrases = document.tokens.filter(_.attr[PennPosTag].isPersonalPronoun).map(t => new Phrase(t.section, start=t.positionInSection, length=1,offsetToHeadToken = -1)).toSeq
    for (phrase <- phrases) phrase.attr += new NounPhraseType(phrase, "PRO")
    phrases
  }
}
