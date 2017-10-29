package cc.factorie.app.nlp.coref

import cc.factorie.variable.LabeledCategoricalVariable

class MentionPairLabel(val model: PairwiseCorefModel, val mention1:Mention, val mention2:Mention, mentions: Seq[Mention], val initialValue: Boolean, options: CorefOptions) extends LabeledCategoricalVariable(if (initialValue) "YES" else "NO") {
  def domain = model.MentionPairLabelDomain

  def genFeatures(): MentionPairFeatures = new MentionPairFeatures(model, mention1, mention2, mentions, options)
}
