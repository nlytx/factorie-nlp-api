package cc.factorie.app.nlp.coref

import cc.factorie.la
import cc.factorie.la.{Tensor1, WeightsMapAccumulator}

class BaseCorefModel extends PairwiseCorefModel {
  val pairwise = Weights(new la.DenseTensor1(MentionPairFeaturesDomain.dimensionDomain.maxSize))
  def predict(pairwiseStats: Tensor1) = pairwise.value dot pairwiseStats
  def accumulateObjectiveGradient(accumulator: WeightsMapAccumulator, features: Tensor1, gradient: Double, weight: Double) = accumulator.accumulate(pairwise, features, gradient * weight)
}
