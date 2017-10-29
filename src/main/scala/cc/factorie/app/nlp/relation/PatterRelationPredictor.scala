package cc.factorie.app.nlp.relation

import java.io.InputStream

import scala.io.Source

case class PatternRelationPredictor(relation : String, patternConfidences : Map[String, Double], qTypes : Set[String],
                                    sTypes : Set[String]) {

  val ARG1 = "$ARG1"
  val ARG2 = "$ARG2"


  /** The first boolean indicates if the relation holds in the forward direction (arg1 first) the second if it holds in the reverse */
  def relationMatch(rm : RelationMention) : Double = {
    val arg1End = rm.arg1.phrase.last.positionInSentence
    val arg2Start = rm.arg2.phrase.head.positionInSentence


    val forwardPattern = ARG1 + " " + rm.arg1.phrase.sentence.slice(arg1End + 1, arg2Start).map(_.string).mkString(" ") + " " + ARG2
    val backwardPattern = ARG2 + " " + rm.arg1.phrase.sentence.slice(arg1End + 1, arg2Start).map(_.string).mkString(" ") + " " + ARG1

    val pattern = if(rm.isArg1First) forwardPattern else backwardPattern

    val arg1Type = rm.arg1.phrase.head.nerTag.baseCategoryValue
    val arg2Type = rm.arg2.phrase.head.nerTag.baseCategoryValue
    val hasMatch = qTypes.contains(arg1Type) && sTypes.contains(arg2Type) && patternConfidences.contains(pattern)
    if(hasMatch) patternConfidences(pattern) else 0.0
  }
}

object PatternRelationPredictor {
  def predictorsFromStreams(patternStream:InputStream, typeFileStream:InputStream):Seq[PatternRelationPredictor] = {

    val relToPats = Source.fromInputStream(patternStream, "UTF8").getLines.map(_.stripLineEnd.split(" ", 3)).
      map(fields => fields(1) -> (fields(2), fields(0).toDouble)).toList.groupBy(_._1).map { case (k,v) => (k,v.map(_._2).toMap)}

    // reads types from a white-space & comma-separted file of the form:
    // relation arg1type,arg1type... arg2type,arg2type
    // Types of ontonotes domain described here: http://catalog.ldc.upenn.edu/docs/LDC2008T04/OntoNotes-Release-2.0.pdf
    val relToTypes = Source.fromInputStream(typeFileStream, "UTF8").getLines.map(_.stripLineEnd.split(" ", 3)).
      map(fields => fields(0) -> (fields(1).split(',').toSet, fields(2).split(',').toSet)).toList
    for ((rel, (arg1types, arg2types)) <- relToTypes) yield
      new PatternRelationPredictor(rel, relToPats.getOrElse(rel, Map.empty[String, Double]), arg1types, arg2types)
  }
}

