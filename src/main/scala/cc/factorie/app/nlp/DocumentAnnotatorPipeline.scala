package cc.factorie.app.nlp

import cc.factorie.util.FastLogging

import scala.reflect.ClassTag




/** A factory for creating DocumentAnnotatorPipelines given requirements about which annotations or which DocumentAnnotators are desired. */
object DocumentAnnotatorPipeline extends FastLogging  {

  val defaultDocumentAnnotationMap: DocumentAnnotatorMap = new collection.immutable.ListMap ++ Seq(
    // Note that order matters here
    classOf[cc.factorie.app.nlp.pos.PennPosTag] -> (() => pos.OntonotesForwardPosTagger),
    classOf[cc.factorie.app.nlp.parse.ParseTree] -> (() => parse.OntonotesTransitionBasedParser),
    classOf[cc.factorie.app.nlp.segment.PlainNormalizedTokenString] -> (() => segment.PlainTokenNormalizer),
    classOf[Token] -> (() => segment.DeterministicNormalizingTokenizer),
    classOf[Sentence] -> (() => segment.DeterministicSentenceSegmenter),
    classOf[cc.factorie.app.nlp.lemma.WordNetTokenLemma] -> (() => lemma.WordNetLemmatizer),
    classOf[cc.factorie.app.nlp.lemma.SimplifyDigitsTokenLemma] -> (() => lemma.SimplifyDigitsLemmatizer),
    classOf[cc.factorie.app.nlp.lemma.CollapseDigitsTokenLemma] -> (() => lemma.CollapseDigitsLemmatizer),
    classOf[cc.factorie.app.nlp.lemma.PorterTokenLemma] -> (() => lemma.PorterLemmatizer),
    classOf[cc.factorie.app.nlp.lemma.LowercaseTokenLemma] -> (() => lemma.LowercaseLemmatizer),
    classOf[cc.factorie.app.nlp.ner.NerTag] -> (() => ner.ConllChainNer), // TODO Should there be a different default?
    classOf[cc.factorie.app.nlp.ner.BilouConllNerTag] -> (() => ner.NoEmbeddingsConllStackedChainNer),
    classOf[cc.factorie.app.nlp.ner.BilouOntonotesNerTag] -> (() => ner.NoEmbeddingsOntonotesStackedChainNer),
    classOf[cc.factorie.app.nlp.ner.ConllNerSpanBuffer] -> (() => ner.BilouConllNerChunkAnnotator),
    classOf[cc.factorie.app.nlp.ner.OntonotesNerSpanBuffer] -> (() => ner.BilouOntonotesNerChunkAnnotator),
    classOf[cc.factorie.app.nlp.phrase.Gender] -> (() => phrase.MentionPhraseGenderLabeler),
    classOf[cc.factorie.app.nlp.phrase.Number] -> (() => phrase.MentionPhraseNumberLabeler),
    classOf[cc.factorie.app.nlp.phrase.DatePhraseList] -> (() => phrase.DatePhraseFinder),
    classOf[cc.factorie.app.nlp.coref.WithinDocCoref] -> (() => coref.NerForwardCoref),
    classOf[cc.factorie.app.nlp.relation.RelationMentionSeq] -> (() => relation.ConllPatternBasedRelationFinder)
  )

  //def apply(goal: Class[_]): DocumentAnnotationPipeline = apply(Seq(goal), defaultDocumentAnnotationMap)
  def apply[A](implicit m:ClassTag[A]): DocumentAnnotationPipeline = apply(defaultDocumentAnnotationMap, Nil, Seq(m.runtimeClass))
  def apply[A,B](implicit m1:ClassTag[A], m2:ClassTag[B]): DocumentAnnotationPipeline = apply(defaultDocumentAnnotationMap, Nil, Seq(m1.runtimeClass, m2.runtimeClass))
  def apply[A,B,C](implicit m1:ClassTag[A], m2:ClassTag[B], m3:ClassTag[C]): DocumentAnnotationPipeline = apply(defaultDocumentAnnotationMap, Nil, Seq(m1.runtimeClass, m2.runtimeClass, m3.runtimeClass))
  def apply[A,B,C,D](implicit m1:ClassTag[A], m2:ClassTag[B], m3:ClassTag[C], m4:ClassTag[D]): DocumentAnnotationPipeline = apply(defaultDocumentAnnotationMap, Nil, Seq(m1.runtimeClass, m2.runtimeClass, m3.runtimeClass, m4.runtimeClass))
  //def apply(goal: Class[_], map: DocumentAnnotatorMap): DocumentAnnotationPipeline = apply(Seq(goal), map)
  def apply[A](map: DocumentAnnotatorMap)(implicit m:ClassTag[A]): DocumentAnnotationPipeline = apply(map, Nil, Seq(m.runtimeClass))
  def apply[A,B](map: DocumentAnnotatorMap)(implicit m1:ClassTag[A], m2:ClassTag[B]): DocumentAnnotationPipeline = apply(map, Nil, Seq(m1.runtimeClass, m2.runtimeClass))
  def apply[A,B,C](map: DocumentAnnotatorMap)(implicit m1:ClassTag[A], m2:ClassTag[B], m3:ClassTag[C]): DocumentAnnotationPipeline = apply(map, Nil, Seq(m1.runtimeClass, m2.runtimeClass, m3.runtimeClass))
  def apply[A,B,C,D](map: DocumentAnnotatorMap)(implicit m1:ClassTag[A], m2:ClassTag[B], m3:ClassTag[C], m4:ClassTag[D]): DocumentAnnotationPipeline = apply(map, Nil, Seq(m1.runtimeClass, m2.runtimeClass, m3.runtimeClass, m4.runtimeClass))

  //def apply(goals:Class[_]*): DocumentAnnotationPipeline = apply(defaultDocumentAnnotationMap, Nil, goals:_*)
  //def apply(prereqs: Seq[Class[_]], goals:Class[_]*): DocumentAnnotationPipeline = apply(defaultDocumentAnnotationMap, prereqs, goals)
  //def forGoals(map:DocumentAnnotatorMap, goals:Class[_]*): DocumentAnnotationPipeline = forGoals(map, Nil, goals)
  def apply(map:DocumentAnnotatorMap, prereqs:Seq[Class[_]], goals:Iterable[Class[_]]): DocumentAnnotationPipeline = {
    val pipeSet = collection.mutable.LinkedHashSet[DocumentAnnotator]()
    val preSet = new scala.collection.mutable.HashSet[Class[_]] ++= prereqs
    def recursiveSatisfyPrereqs(goal: Class[_]) {
      if (!preSet.contains(goal) && (!preSet.exists(x => goal.isAssignableFrom(x)))) {
        val provider = if (map.contains(goal)) map(goal)() else {
          val list = map.keys.filter(k => goal.isAssignableFrom(k))
          assert(list.nonEmpty, s"Could not find annotator for goal $goal, map includes ${map.keys.mkString(", ")}")
          map(list.head)()
        }
        if (!pipeSet.contains(provider)) {
          provider.prereqAttrs.foreach(recursiveSatisfyPrereqs)
          provider.postAttrs.foreach(preSet += _)
          pipeSet += provider
        }
      }
    }
    goals.foreach(recursiveSatisfyPrereqs)
    checkPipeline(pipeSet.toSeq)
    new DocumentAnnotationPipeline(pipeSet.toSeq)
  }

  def apply(annotators:DocumentAnnotator*): DocumentAnnotationPipeline = apply(defaultDocumentAnnotationMap, Nil, annotators:_*)
  def apply(map:DocumentAnnotatorMap, annotators:DocumentAnnotator*): DocumentAnnotationPipeline = apply(map, Nil, annotators:_*)
  def apply(map:DocumentAnnotatorMap, prereqs:Seq[Class[_]], annotators: DocumentAnnotator*): DocumentAnnotationPipeline = {
    val other = new MutableDocumentAnnotatorMap
    map.foreach(k => other += k)
    annotators.foreach(a => other += a) // By being added later, these annotators will overwrite the default ones when there is an overlap
    apply(map=other, prereqs, annotators.flatMap(_.postAttrs))
  }

  def checkPipeline(pipeline: Seq[DocumentAnnotator]) {
    if (logger.level == cc.factorie.util.Logger.DEBUG) {
      logger.debug("-- printing pipeline --")
      for (annotator <- pipeline) {
        logger.debug(s"Annotator ${annotator.getClass.getName} Prereqs(${annotator.prereqAttrs.map(_.getName).mkString(", ")}}) PostAttrs(${annotator.postAttrs.map(_.getName).mkString(", ")})")
      }
    }
    val satisfiedSet = collection.mutable.HashSet[Class[_]]()
    for (annotator <- pipeline) {
      for (requirement <- annotator.prereqAttrs
           if !satisfiedSet.contains(requirement)
           if !satisfiedSet.exists(c => requirement.isAssignableFrom(c)))
        assert(1 == 0, s"Prerequisite $requirement not satisfied before $annotator gets called in pipeline ${pipeline.mkString(" ")}")
      for (provision <- annotator.postAttrs) {
        assert(!satisfiedSet.contains(provision), s"Pipeline attempting to provide $provision twice. Pipeline: ${pipeline.mkString(" ")}")
        satisfiedSet += provision
      }
    }
  }
}