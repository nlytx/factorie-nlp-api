package cc.factorie.app.nlp.relation

import cc.factorie.app.nlp.coref.{ParseForwardCoref, WithinDocCoref}
import cc.factorie.app.nlp.{Document, DocumentAnnotator, Token, TokenSpan}

/**
  * @author John Sullivan, Benjamin Roth
  */
class PatternBasedRelationFinder(predictors:Seq[PatternRelationPredictor]) extends DocumentAnnotator{
  def tokenAnnotationString(token: Token) = null

  def postAttrs = Seq(classOf[RelationMentionSeq])

  def prereqAttrs = (Seq(classOf[WithinDocCoref]) ++ ParseForwardCoref.prereqAttrs).distinct

  def process(doc: Document) = {
    val coref = doc.coref

    val mentions = coref.mentions.sortBy(_.phrase.asInstanceOf[TokenSpan]).toList

    /** this produces a sliding window of 4 mentions that we then compare to generate contexts. Each mention should be compared
      * to the three mentions before and after it in the following loop. The last element is a singleton list which we drop.
      * The last mention in the document has already been compared to the three mentions that precede it.
      */
    val mentionGrouping = (0 until mentions.size).map(idx => mentions.slice(idx, math.min(idx + 4, mentions.size))).dropRight(1).toList

    val relationMentions = (for(m1 :: ms <- mentionGrouping;
                                m2 <- ms;
                                if ((m1.phrase.sentence eq m2.phrase.sentence) && (m1.phrase.sentence.length < 100)))
      yield {Seq(new RelationMention(m1, m2, true), new RelationMention(m2, m1, false))}).flatten

    for (rm <- relationMentions;
         predictor <- predictors;
         matchLevel = predictor.relationMatch(rm);
         if matchLevel > 0.0) {
      rm._relations.+=(TACRelation(predictor.relation, matchLevel, rm.arg1.phrase.sentence.string))
    }

    val relSet = new RelationMentionSeq()
    relSet.++=(relationMentions.filter(_._relations.nonEmpty))
    doc.attr += relSet
    doc
  }
}