package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.phrase._


/** Forward Coreference on Ner and Pronoun Mentions*/
class NerForwardCoref extends ForwardCoref {
  override def prereqAttrs: Seq[Class[_]] = (ConllPhraseFinder.prereqAttrs ++ AcronymNounPhraseFinder.prereqAttrs++PronounFinder.prereqAttrs ++ NnpPosNounPhraseFinder.prereqAttrs ++ ForwardCoref.prereqAttrs).distinct
  override def annotateMentions(document:Document): Unit = {
    if(document.coref.mentions.isEmpty) (ConllPhraseFinder(document) ++ PronounFinder(document) ++ NnpPosNounPhraseFinder(document)++ AcronymNounPhraseFinder(document)).distinct.foreach(phrase => document.getCoref.addMention(phrase))
    document.coref.mentions.foreach(mention => NounPhraseEntityTypeLabeler.process(mention.phrase))
    document.coref.mentions.foreach(mention => NounPhraseGenderLabeler.process(mention.phrase))
    document.coref.mentions.foreach(mention => NounPhraseNumberLabeler.process(mention.phrase))
  }
}

object NerForwardCoref extends NerForwardCoref {
  //val stream:InputStream = new DataInputStream(ClasspathURL[NerForwardCoref](".factorie").openConnection().getInputStream)
  //deserialize(stream)
}