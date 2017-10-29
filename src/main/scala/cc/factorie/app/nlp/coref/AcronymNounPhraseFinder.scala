package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.phrase.{ConllPhraseEntityType, NounPhraseType, Phrase}
import cc.factorie.app.nlp.{Document, Token}

import scala.collection.mutable

/** Apply returns a list of acronym noun phrases.
  *
  * @author Andrew McCallum */
object AcronymNounPhraseFinder extends MentionPhraseFinder {
  def prereqAttrs = Seq(classOf[Token])
  def apply(doc:Document): Seq[Phrase] = {
    val result = new mutable.ArrayBuffer[Phrase]
    for (section <- doc.sections; token <- section.tokens) {
      // Matches middle word of "Yesterday IBM announced" but not "OBAMA WINS ELECTION"
      if ( token.string.length > 2 && !token.containsLowerCase && Character.isUpperCase(token.string(0)) && (token.getNext ++ token.getPrev).exists(_.containsLowerCase)) {
        val phrase = new Phrase(section, token.positionInSection, length=1,offsetToHeadToken = -1)
        phrase.attr += new ConllPhraseEntityType(phrase, "ORG")
        phrase.attr += new NounPhraseType(phrase, "NAM")
        result += phrase
      }
    }
    result
  }
}
