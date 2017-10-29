package cc.factorie.app.nlp.phrase

import cc.factorie.app.nlp.ner.OntonotesEntityTypeDomain
import cc.factorie.variable.LabeledCategoricalVariable

/** Categorical variable indicating whether the noun phrase is person, location, organization, etc.
  * according to the Ontonotes entity type domain. */
class OntonotesEntityType(targetValue:String, val exactMatch:Boolean = false) extends LabeledCategoricalVariable(targetValue) {
  def domain = OntonotesEntityTypeDomain

}
