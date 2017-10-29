package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.phrase.{NounPhraseEntityTypeLabeler, NounPhraseGenderLabeler, NounPhraseNumberLabeler, ParseAndNerBasedPhraseFinder}

/**Forward Coreference on Proper Noun, Pronoun and Common Noun Mentions*/
class ParseForwardCoref extends ForwardCoref {
  override def prereqAttrs: Seq[Class[_]] = ParseAndNerBasedPhraseFinder.prereqAttrs.toSeq ++ ForwardCoref.prereqAttrs
  override def annotateMentions(document:Document): Unit = {
    if(document.coref.mentions.isEmpty) ParseAndNerBasedPhraseFinder.getPhrases(document).foreach(document.coref.addMention)
    document.coref.mentions.foreach(mention => NounPhraseEntityTypeLabeler.process(mention.phrase))
    document.coref.mentions.foreach(mention => NounPhraseGenderLabeler.process(mention.phrase))
    document.coref.mentions.foreach(mention => NounPhraseNumberLabeler.process(mention.phrase))
  }
}

object ParseForwardCoref extends ParseForwardCoref {
  //TODO This is commented out for now, but needs investigation
  //deserialize(new DataInputStream(ClasspathURL[ParseForwardCoref](".factorie").openConnection().getInputStream))
}