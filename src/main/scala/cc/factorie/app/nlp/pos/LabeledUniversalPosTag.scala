package cc.factorie.app.nlp.pos

import cc.factorie.app.nlp.Token
import cc.factorie.variable.CategoricalLabeling

/** A categorical variable, associated with a token, holding its Google Universal part-of-speech category,
  * which also separately holds its desired correct "target" value.  */
class LabeledUniversalPosTag(token:Token, targetValue:String) extends UniversalPosTag(token, targetValue) with CategoricalLabeling[String]

