package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Token

class ConllNerTag(token:Token, initialCategory:String) extends NerTag(token, initialCategory) { def domain = ConllNerDomain }