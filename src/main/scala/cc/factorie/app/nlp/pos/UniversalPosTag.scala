package cc.factorie.app.nlp.pos

import cc.factorie.app.nlp.Token
import cc.factorie.variable.CategoricalVariable

/** A categorical variable, associated with a token, holding its Google Universal part-of-speech category.  */
class UniversalPosTag(val token:Token, initialValue:String) extends CategoricalVariable(initialValue) {
  def this(token:Token, other:PennPosTag) = this(token, UniversalPosDomain.categoryFromPenn(other.categoryValue))
  def domain = UniversalPosDomain
}