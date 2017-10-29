package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Token
import cc.factorie.variable.CategoricalLabeling

class LabeledBilouOntonotesNerTag(token:Token, initialCategory:String)
  extends BilouOntonotesNerTag(token, initialCategory) with CategoricalLabeling[String] with Serializable
