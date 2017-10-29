package cc.factorie.app.nlp.ner

import cc.factorie.variable.EnumDomain

object ConllNerDomain extends EnumDomain {
  val O, PER, ORG, LOC, MISC = Value
  freeze()
}
