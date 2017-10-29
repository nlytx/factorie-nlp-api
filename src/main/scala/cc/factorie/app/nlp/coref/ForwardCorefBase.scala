package cc.factorie.app.nlp.coref

import java.util.concurrent.ExecutorService

import cc.factorie.app.nlp.Document
import cc.factorie.optimize.{Example, GradientOptimizer, MiniBatchExample}

import scala.collection.mutable.ArrayBuffer

abstract class ForwardCorefBase extends CorefSystem[Seq[MentionPairLabel]] {
  val options = new CorefOptions
  val model:PairwiseCorefModel


  /**Store head words which are seen over a default 20 times in the model
    * @param trainDocs Documents to generate counts from*/
  def preprocessCorpus(trainDocs:Seq[Document]) = {
    val nonPronouns = trainDocs.flatMap(_.targetCoref.mentions.filterNot(m => m.phrase.isPronoun))
    model.CorefTokenFrequencies.counter = new TopTokenFrequencies(TokenFreqs.countWordTypes(nonPronouns,(t) => t.phrase.headToken.string.toLowerCase,20))
  }

  def instantiateModel(optimizer:GradientOptimizer,pool:ExecutorService) = new LeftRightParallelTrainer(optimizer,pool)

  /**Generate the labels used for training
    * @param coref This is expected to be the true coreference class for the document
    * @return Sequence of training labels for this document*/
  def getCorefStructure(coref:WithinDocCoref): Seq[MentionPairLabel] = {
    val mentions = coref.mentions.sortBy(m=>m.phrase.start)
    assertSorted(mentions)
    val labels = new ArrayBuffer[MentionPairLabel]
    for (i <- 0 until mentions.size){
      if(!options.usePronounRules || !mentions(i).phrase.isPronoun)
        labels ++= generateTrainingLabelsForOneAnaphor(mentions, i)
    }
    labels
  }

  //copy from coref tester
  private def assertSorted(mentions: Seq[Mention]): Unit = {
    for(i <- 0 until mentions.length -1)
      assert(mentions(i).phrase.tokens.head.stringStart <= mentions(i+1).phrase.tokens.head.stringStart, "the mentions are not sorted by their position in the document. Error at position " +i+ " of " + mentions.length)
  }

  /**
    * Given the index of a mention, create positive and negative labels for this mention and its prodecessors
    * @param orderedMentions Mentions for this document
    * @param anaphorIndex Index of current mention to generate labels for
    * @return Training Labels for this Mention */
  protected def generateTrainingLabelsForOneAnaphor(orderedMentions: Seq[Mention], anaphorIndex: Int): Seq[MentionPairLabel] = {
    val labels = new ArrayBuffer[MentionPairLabel]
    val m1 = orderedMentions(anaphorIndex)
    var numAntecedents = 0
    var i = anaphorIndex - 1
    while (i >= 0 && (numAntecedents < options.numPositivePairsTrain || !options.pruneNegTrain)) {
      val m2 = orderedMentions(i)
      val label = m1.entity != null & m1.entity == m2.entity
      if (!pruneMentionPairTraining(m1,m2,label,numAntecedents)) {
        val cl = new MentionPairLabel(model, m1, m2, orderedMentions, label, options=options)
        if(label) numAntecedents += 1
        labels += cl
      }
      i -= 1
    }
    labels
  }
  case class MentionPairLabelFeatures(label: MentionPairLabel,features: MentionPairFeatures)

  /** Given a sequence of MentionPairLabels for a document, compute features of the pair and return both*/
  protected def generateFeatures(labels: Seq[MentionPairLabel]): Seq[MentionPairLabelFeatures] = {
    val previousLabels = new ArrayBuffer[MentionPairLabelFeatures]()
    labels.foreach{ label =>
      val candidateLabelFeatures = label.genFeatures()
      //If we want to merge features of our antecedent with any of it's previous mentions,
      if(options.mergeFeaturesAtAll && label.mention2.entity != null){
        val matchingPreviousLabelsFeatures = previousLabels.lastIndexWhere(l => l.label.mention2.entity == label.mention2.entity)
        if(matchingPreviousLabelsFeatures != -1) mergeFeatures(candidateLabelFeatures, previousLabels(matchingPreviousLabelsFeatures).features)
      }
      previousLabels += new MentionPairLabelFeatures(label,candidateLabelFeatures)
    }
    previousLabels
  }

  class LeftRightParallelTrainer(optimizer: GradientOptimizer, pool: ExecutorService, miniBatchSize: Int = 1) extends ParallelTrainer(optimizer,pool){
    def map(in: Seq[MentionPairLabel]): Seq[Example] = {
      // |**("Adding Features for Labels")
      val examples = MiniBatchExample(miniBatchSize,generateFeatures(in).map{trainingInstance => model.getExample(trainingInstance.label,trainingInstance.features,options.slackRescale)})
      // **|
      examples
    }
  }

  def mergeFeatures(l: MentionPairFeatures, mergeables: MentionPairFeatures) {
    if (options.mergeFeaturesAtAll) {
      assert(l.features.activeCategories.forall(!_.startsWith("NBR")))
      val mergeLeft = ArrayBuffer[MentionPairFeatures]()
      l.features ++= mergeables.features.mergeableAllFeatures.map("NBRR_" + _)
    }
  }

  /**Types of Pairs Pruned during Training
    *     - cataphora since we do not corefer these
    *     - Any pair of mentions which overlap each other*/
  def pruneMentionPairTraining(anaphor: Mention,antecedent: Mention,label: Boolean,numAntecedents: Int): Boolean = {
    val cataphora = antecedent.phrase.isPronoun && !anaphor.phrase.isPronoun
    if(cataphora) {
      if (label && !options.allowPosCataphora || !label && !options.allowNegCataphora) {
        return true
      }
    }
    if(!anaphor.phrase.tokens.intersect(antecedent.phrase.tokens).isEmpty) return true
    if (label && numAntecedents > 0 && !options.pruneNegTrain) return true
    return false
  }
  def pruneMentionPairTesting(anaphor: Mention,antecedent: Mention): Boolean = {
    val cataphora = antecedent.phrase.isPronoun && !anaphor.phrase.isPronoun
    if(options.usePronounRules && antecedent.phrase.isPronoun) return true
    else if(cataphora || options.allowTestCataphora) return true
    if(!anaphor.phrase.tokens.intersect(antecedent.phrase.tokens).isEmpty) return true
    return false
  }

  /**Find each mentions best scoring antecedent.  If the antecedent has a cluster add the new mention if not, create a new entity and add both mentions
    * Currently does not create singleton entities
    * @param coref Expects nontarget coref class that is pre annotated with mentions
    * @return
    */
  def infer(coref: WithinDocCoref): WithinDocCoref = {
    val mentions = coref.mentions.sortBy(m => m.phrase.start)
    for (i <- 0 until coref.mentions.size) {
      val m1 = mentions(i)
      val bestCand = getBestCandidate(coref,mentions, i)
      if (bestCand != null) {
        if(bestCand.entity ne null){
          bestCand.entity += m1
        }
        else{
          val entity = coref.newEntity(); entity += bestCand; entity += m1
        }
      }else {val entity = coref.newEntity(); entity += m1}
    }
    coref
  }

  def getBestCandidate(coref: WithinDocCoref, mentions: Seq[Mention], mInt: Int): Mention = {
    val candidateLabels = ArrayBuffer[MentionPairFeatures]()
    var bestCandidate: Mention = null
    var bestScore = Double.MinValue
    var anteIdx = mInt
    val m1 = mentions(mInt)
    var numPositivePairs = 0
    while (anteIdx >= 0 && (numPositivePairs < options.numPositivePairsTest || !options.pruneNegTest)) {
      val m2 = mentions(anteIdx)
      if (!pruneMentionPairTesting(m1,m2)) {
        val candidateLabel = new MentionPairFeatures(model, m1, m2, mentions, options=options)
        val mergeables = candidateLabels.lastIndexWhere(l => l.mention2.entity != null &&l.mention2.entity == candidateLabel.mention2.entity)
        if(mergeables != -1) mergeFeatures(candidateLabel, candidateLabels(mergeables))
        candidateLabels += candidateLabel
        val score =  if (m1.phrase.isProperNoun && m1.attr[MentionCharacteristics].nounWords.forall(m2.attr[MentionCharacteristics].nounWords.contains)
          && m2.attr[MentionCharacteristics].nounWords.forall(m1.attr[MentionCharacteristics].nounWords.contains)
          || options.mergeMentionWithApposition && (m1.phrase.isAppositionOf(m2.phrase)
          || m2.phrase.isAppositionOf(m1.phrase))) Double.PositiveInfinity
        else model.predict(candidateLabel.value)
        if (score > 0.0) {
          numPositivePairs += 1
          if (bestScore <= score) {
            bestCandidate = m2
            bestScore = score
          }
        }
      }
      anteIdx -= 1
    }
    bestCandidate
  }
}

