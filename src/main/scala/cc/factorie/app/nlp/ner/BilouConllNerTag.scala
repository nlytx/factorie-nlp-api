package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Token

class BilouConllNerTag(token:Token, initialCategory:String) extends NerTag(token, initialCategory) with Serializable {
  def domain = BilouConllNerDomain
}
