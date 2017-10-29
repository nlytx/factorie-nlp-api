package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Token
import cc.factorie.variable.CategoricalLabeling

class LabeledBilouConllNerTag(token:Token, initialCategory:String)
  extends BilouConllNerTag(token, initialCategory) with CategoricalLabeling[String] with Serializable
