package cc.factorie.app.nlp.coref

/**
  * Created by andrew@andrewresearch.net on 28/10/17.
  */

/** Trainers for Coreference Systems*/
trait ForwardCorefTrainerOpts extends CorefTrainerOpts{
  val numPositivePairsTrain = new CmdOption("prune-train", 2, "INT", "number of positive pairs before pruning instances in training")
  val numPositivePairsTest = new CmdOption("prune-test", 100, "INT", "number of positive pairs before pruning instances in testing")
  val numThreads = new CmdOption("num-threads", 4, "INT", "Number of threads to use")
  val featureComputationsPerThread = new CmdOption("feature-computations-per-thread", 2, "INT", "Number of feature computations per thread to run in parallel during training")
  val numTrainingIterations = new CmdOption("num-training-iterations", 4, "INT", "Number of passes through the training data")
  val useMIRA = new CmdOption("use-mira", false, "BOOLEAN", "Whether to use MIRA as an optimizer")
  val saveFrequency = new CmdOption("save-frequency", 1, "INT", "how often to save the model between epochs")
  val trainPortionForTest = new CmdOption("train-portion-for-test", 0.1, "DOUBLE", "When testing on train, what portion to use.")
  val mergeFeaturesAtAll = new CmdOption("merge-features-at-all", true, "BOOLEAN", "Whether to merge features")
  val conjunctionStyle = new CmdOption("conjunction-style", "NONE", "NONE|HASH|SLOW", "What types of conjunction features to use - options are NONE, HASH, and SLOW (use slow string-based conjunctions).")
  val entityLR = new CmdOption("entity-left-right",false,"BOOLEAN","whether to do entity-based pruning in lr search")
  val slackRescale = new CmdOption("slack-rescale",2.0,"FLOAT","recall bias for hinge loss")
  val useEntityType = new CmdOption("use-entity-type",true,"BOOLEAN","whether to use entity type info")
  val mergeAppositions = new CmdOption("merge-appositions",false,"BOOLEAN","whether to merge appositions as a rule")
  val usePronounRules = new CmdOption("use-pronoun-rules",false,"BOOLEAN","whether to do deterministic assigning of pronouns and not consider pronouns for training")
  val trainSeparatePronounWeights = new CmdOption("separate-pronoun-weights",true,"BOOLEAN","train a separate weight vector for pronoun-pronoun comparison")
  val numCompareToTheLeft = new CmdOption("num-compare-to-the-left",75,"INT","number of mentions to compare to the left before backing off to only looking at non-pronouns and those in entities (only used if entityLR == true)")
  val learningRate = new CmdOption("learning-rate",1.0,"FLOAT","learning rate")
  val serialize = new CmdOption("serialize", "ForwardCoref.factorie", "FILE", "Filename in which to serialize classifier.")
  val deserialize = new CmdOption("deserialize", "", "FILE", "Filename from which to deserialize classifier.")
  val useAverageIterate = new CmdOption("use-average-iterate", true, "BOOLEAN", "Use the average iterate instead of the last iterate?")
}
