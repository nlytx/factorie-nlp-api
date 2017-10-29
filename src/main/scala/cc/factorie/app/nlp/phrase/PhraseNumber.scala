package cc.factorie.app.nlp.phrase

class PhraseNumber(val phrase:Phrase, value:Int) extends Number(value) {
  def this(phrase:Phrase, value:String) = this(phrase, NumberDomain.index(value))
}

