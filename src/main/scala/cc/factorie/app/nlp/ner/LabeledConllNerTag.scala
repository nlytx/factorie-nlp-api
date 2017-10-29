package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Token
import cc.factorie.variable.CategoricalLabeling

class LabeledConllNerTag(token:Token, initialCategory:String) extends ConllNerTag(token, initialCategory) with CategoricalLabeling[String]
