package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Token
import cc.factorie.variable.CategoricalLabeling

class LabeledOntonotesNerTag(token:Token, initialCategory:String)
  extends OntonotesNerTag(token, initialCategory) with CategoricalLabeling[String]