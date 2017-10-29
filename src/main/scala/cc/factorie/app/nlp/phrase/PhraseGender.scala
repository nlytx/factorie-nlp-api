package cc.factorie.app.nlp.phrase

class PhraseGender(val phrase:Phrase, categoryIndex:Int) extends Gender(categoryIndex) {
  def this(phrase:Phrase, category:String) = this(phrase, GenderDomain.index(category))
}
