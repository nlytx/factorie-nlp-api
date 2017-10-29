package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.{Section, _}
import cc.factorie.variable.CategoricalDomain

object BilouOntonotesNerDomain extends CategoricalDomain[String] with BILOU {
  this ++= encodedTags(OntonotesNerDomain.categories)
  freeze()
  // Convert from an intValue in this domain to an intValue in the OntonotesNerDomain
  def bilouSuffixIntValue(bilouIntValue:Int): Int = if (bilouIntValue == 0) 0 else ((bilouIntValue - 1) / 4) + 1
  def spanList(section:Section): OntonotesNerSpanBuffer = {
    val boundaries = bilouBoundaries(section.tokens.map(_.attr[BilouOntonotesNerTag].categoryValue))
    new OntonotesNerSpanBuffer ++= boundaries.map(b => new OntonotesNerSpan(section, b._1, b._2, b._3))
  }
}
