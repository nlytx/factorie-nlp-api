package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Token

class OntonotesNerTag(token:Token, initialCategory:String) extends NerTag(token, initialCategory) {
  def domain = OntonotesNerDomain
}