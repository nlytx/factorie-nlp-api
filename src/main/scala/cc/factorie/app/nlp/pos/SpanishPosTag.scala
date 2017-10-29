/* Copyright (C) 2008-2016 University of Massachusetts Amherst.
   This file is part of "FACTORIE" (Factor graphs, Imperative, Extensible)
   http://factorie.cs.umass.edu, http://github.com/factorie
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package cc.factorie.app.nlp.pos

/*
import cc.factorie.app.nlp._
import cc.factorie.variable._








/** Penn Treebank part-of-speech tag domain. */
object SpanishPosDomain extends CategoricalDomain[String] {
  this ++= Vector(
    "a", // adjective
    "c", // conjunction
    "d", // determiner
    "f", // punctuation
    "i", // interjection
    "n", // noun
    "p", // pronoun
    "r", // adverb
    "s", // preposition
    "v", // verb
    "w", // date
    "z", // number
    "_" // unknown
  )
  freeze()

  def isNoun(pos:String): Boolean = pos(0) == 'n'
//  def isProperNoun(pos:String) = { pos == "NNP" || pos == "NNPS" }
  def isVerb(pos:String) = pos(0) == 'v'
  def isAdjective(pos:String) = pos(0) == 'a'
//  def isPersonalPronoun(pos: String) = pos == "PRP"
}
/** A categorical variable, associated with a token, holding its Penn Treebank part-of-speech category.  */
class SpanishPosTag(token:Token, initialIndex:Int) extends PosTag(token, initialIndex) {
  def this(token:Token, initialCategory:String) = this(token, SpanishPosDomain.index(initialCategory))
  final def domain = SpanishPosDomain
  def isNoun = SpanishPosDomain.isNoun(categoryValue)
//  def isProperNoun = SpanishPosDomain.isProperNoun(categoryValue)
  def isVerb = SpanishPosDomain.isVerb(categoryValue)
  def isAdjective = SpanishPosDomain.isAdjective(categoryValue)
//  def isPersonalPronoun = SpanishPosDomain.isPersonalPronoun(categoryValue)
}

/** A categorical variable, associated with a token, holding its Spanish Treebank part-of-speech category,
    which also separately holds its desired correct "target" value.  */
class LabeledSpanishPosTag(token:Token, targetValue:String) extends SpanishPosTag(token, targetValue) with CategoricalLabeling[String]

*/