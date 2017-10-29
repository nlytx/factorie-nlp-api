package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.{Section, _}
import cc.factorie.variable.CategoricalDomain

object BilouConllNerDomain extends CategoricalDomain[String] with BILOU {
  this ++= encodedTags(ConllNerDomain.categories)
  freeze()
  def spanList(section:Section): ConllNerSpanBuffer = {
    val boundaries = bilouBoundaries(section.tokens.map(_.attr[BilouConllNerTag].categoryValue))
    new ConllNerSpanBuffer ++= boundaries.map(b => new ConllNerSpan(section, b._1, b._2, b._3))
  }
}
