package cc.factorie.app.nlp.coref

import java.util.concurrent.ExecutorService

import cc.factorie.app.nlp.lexicon.{LexiconsProvider, StaticLexicons}
import cc.factorie.app.nlp.phrase._
import cc.factorie.app.nlp.pos.PennPosTag
import cc.factorie.app.nlp.{Document, DocumentAnnotator, Token}
import cc.factorie.optimize._
import cc.factorie.util.Trackable


/**Base class for any coreference system
  *
  * @tparam CoreferenceStructure The type used as a training instance, ex. MentionPairLabel or MentionGraph,
  *                              In the examples above, the training instance is either one pair or the whole document respectively*/
abstract class CorefSystem[CoreferenceStructure] extends DocumentAnnotator with Trackable{
  val model:CorefModel
  val options:CorefOptions
  def prereqAttrs: Seq[Class[_]] = Seq(classOf[Token],classOf[PennPosTag])
  def postAttrs = Seq(classOf[WithinDocCoref])
  def tokenAnnotationString(token:Token): String = {
    val entities = token.document.coref.entities.toSeq
    var outputString = token.document.coref.mentions.filter(mention => mention.phrase.contains(token)) match {
      case ms:Seq[Mention] if ms.length > 0 => ms.filter(m => m.entity != null && !m.entity.isSingleton).map{
        m => if (m.phrase.length == 1) "("+entities.indexOf(m.entity)+")"
        else if(m.phrase.indexOf(token) == 0) "("+entities.indexOf(m.entity)
        else if(m.phrase.indexOf(token) == m.phrase.length - 1) entities.indexOf(m.entity)+")"
        else ""
      }.mkString("|")
      case _ => "_"
    }
    if(outputString == "") outputString = "_"
    else if(outputString.endsWith("|")) outputString = outputString.substring(0,outputString.length-1)
    "%15s".format(outputString)
  }

  def process(document: Document) = {
    document.annotators += classOf[WithinDocCoref] -> this.getClass
    if(document.getCoref.mentions.isEmpty)
      annotateMentions(document)
    infer(document.getCoref)
    document
  }

  def annotateMentions(document: Document): Unit = {
    if(options.useGoldBoundaries){
      assert(document.targetCoref ne null,"Gold Boundaries cannot be used without gold data.")
      document.targetCoref.mentions.foreach{m =>
        if(options.useEntityType){
          val newMention = document.getCoref.addMention(new Phrase(m.phrase.value.chain,m.phrase.start,m.phrase.length,m.phrase.headTokenOffset))
          newMention.phrase.attr += m.phrase.attr[OntonotesPhraseEntityType]
          newMention.phrase.attr += m.phrase.attr[NounPhraseType]
        }
        else {
          val newMention = document.getCoref.addMention(new Phrase(m.phrase.value.chain,m.phrase.start,m.phrase.length,m.phrase.headTokenOffset))
          NounPhraseEntityTypeLabeler.process(newMention.phrase)
          newMention.phrase.attr += m.phrase.attr[NounPhraseType]
        }
      }
      NounPhraseGenderLabeler.process(document)
      MentionPhraseNumberLabeler.process(document)
    }
  }

  /**Perform any preprocessing such as getting top used words
    * @param trainDocs Documents to generate counts from */
  def preprocessCorpus(trainDocs: Seq[Document]): Unit

  /**Returns training labels for data in the format that should be used for training
    * @param coref Gold Coref to be used for training */
  def getCorefStructure(coref: WithinDocCoref): CoreferenceStructure
  def instantiateModel(optimizer: GradientOptimizer,pool: ExecutorService): ParallelTrainer
  def infer(doc: WithinDocCoref): WithinDocCoref

  abstract class ParallelTrainer(optimizer: GradientOptimizer, val pool: ExecutorService) {
    def map(in: CoreferenceStructure): Seq[Example]
    def reduce(states: Iterable[Seq[Example]]) {
      for (examples <- states) {
        val trainer = new OnlineTrainer(model.parameters, optimizer, maxIterations = 1, logEveryN = examples.length - 1)
        trainer.trainFromExamples(examples)
      }
    }
    def runParallel(ins: Seq[CoreferenceStructure]){
      reduce(cc.factorie.util.Threading.parMap(ins, pool)(map))
    }
    def runSequential(ins: Seq[CoreferenceStructure]){
      reduce(ins.map(map))
    }
  }


  // todo fix this
  @deprecated("This exists to preserve prior behavior, it should be a constructor argument", "10/5/15")
  val lexicon = new StaticLexicons()(LexiconsProvider.classpath())

  // No training in this library
/*
  def train(trainDocs: Seq[Document], testDocs: Seq[Document], wn: WordNet, rng: scala.util.Random, saveModelBetweenEpochs: Boolean,saveFrequency: Int,filename: String, learningRate: Double = 1.0): Double =  {
    val optimizer = if (options.useAverageIterate) new AdaGrad(learningRate) with ParameterAveraging else if (options.useAdaGradRDA) new AdaGradRDA(rate = learningRate,l1 = options.l1) else new AdaGrad(rate = learningRate)
    for(doc <- trainDocs; mention <- doc.targetCoref.mentions) mention.attr += new MentionCharacteristics(mention, lexicon)
    preprocessCorpus(trainDocs)
    |**("Training Structure Generated")
    var i = 0
    val trainingFormat: Seq[CoreferenceStructure] = trainDocs.map{doc => i +=1 ; if(i % 100 == 0) println("Processing Labels for: " + i + " of " + trainDocs.size); getCorefStructure(doc.targetCoref)}
    **|
    val pool = java.util.concurrent.Executors.newFixedThreadPool(options.numThreads)
    var accuracy = 0.0
    try {
      val trainer = instantiateModel(optimizer, pool)
      for (iter <- 0 until options.numTrainingIterations) {
        val shuffledDocs = rng.shuffle(trainingFormat)
        val batches = shuffledDocs.grouped(options.featureComputationsPerThread*options.numThreads).toSeq
        for ((batch, b) <- batches.zipWithIndex) {
          if (options.numThreads > 1) trainer.runParallel(batch)
          else trainer.runSequential(batch)
        }
        if (!model.MentionPairFeaturesDomain.dimensionDomain.frozen) model.MentionPairFeaturesDomain.dimensionDomain.freeze()
        if (!options.useAdaGradRDA && options.useAverageIterate) optimizer match {case o: ParameterAveraging => o.setWeightsToAverage(model.parameters) }
        println("Train docs")
        doTest(trainDocs.take((trainDocs.length*options.trainPortionForTest).toInt), wn, "Train")
        println("Test docs")
        |**("Running Test")
        accuracy = doTest(testDocs, wn, "Test")
        **|("End Test")
        if(saveModelBetweenEpochs && iter % saveFrequency == 0)
          serialize(filename + "-" + iter)
        if (!options.useAdaGradRDA && options.useAverageIterate) optimizer match {case o: ParameterAveraging => o.unSetWeightsToAverage(model.parameters) }
      }
      if (!options.useAdaGradRDA&& options.useAverageIterate) optimizer match {case o: ParameterAveraging => o.setWeightsToAverage(model.parameters) }
      accuracy
    } finally {
      pool.shutdown()
    } */
  }