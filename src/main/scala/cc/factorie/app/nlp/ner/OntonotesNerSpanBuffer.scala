package cc.factorie.app.nlp.ner

class OntonotesNerSpanBuffer(spans:Iterable[OntonotesNerSpan]) extends NerSpanBuffer[OntonotesNerSpan] with Serializable {
  def this() = this(Iterable.empty[OntonotesNerSpan])
}
