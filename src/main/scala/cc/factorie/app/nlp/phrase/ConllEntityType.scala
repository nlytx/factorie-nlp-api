package cc.factorie.app.nlp.phrase

import cc.factorie.app.nlp.ner.ConllNerDomain
import cc.factorie.variable.{CategoricalLabeling, CategoricalVariable}

/** Categorical variable indicating whether the noun phrase is person, location, organization, etc.
  * according to the CoNLL 2003 entity type domain: PER, ORG, LOC, MISC. */
class ConllEntityType(targetIndex:Int) extends CategoricalVariable[String](targetIndex) with CategoricalLabeling[String] {
  def this(targetCategory:String) = this(ConllNerDomain.index(targetCategory))
  def domain = ConllNerDomain
}