package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.ner.{NerSpan, NerSpanBuffer}
import cc.factorie.app.nlp.phrase.Phrase

class NerPhraseFinder[Span <: NerSpan] extends MentionPhraseFinder {
  val prereqAttrs = Seq(classOf[NerSpanBuffer[Span]])
  def apply(doc:Document):Seq[Phrase] =
    doc.attr[NerSpanBuffer[Span]].map(new Phrase(_))
}
