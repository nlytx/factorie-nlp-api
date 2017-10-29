package cc.factorie.app.nlp.coref

import cc.factorie.app
import cc.factorie.la.Tensor1
import cc.factorie.optimize.{Example, OptimizableObjectives, PredictorExample}

abstract class PairwiseCorefModel extends app.classify.backend.OptimizablePredictor[Double,Tensor1] with CorefModel{
  def getExample(label: MentionPairLabel,features:MentionPairFeatures, scale: Double): Example = new PredictorExample(this, features.value, if (label.target.categoryValue == "YES") 1 else -1, OptimizableObjectives.hingeScaledBinary(1.0, 3.0))
}
