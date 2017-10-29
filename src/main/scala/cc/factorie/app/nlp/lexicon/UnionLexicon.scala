package cc.factorie.app.nlp.lexicon

import cc.factorie.app.chain.Observation
import cc.factorie.app.nlp.lemma.Lemmatizer
import cc.factorie.app.strings.StringSegmenter

/** a union of many PhraseLexicons
  *
  * @author Kate Silverstein */
class UnionLexicon(val name: String, val members: PhraseLexicon*) extends MutableLexicon {
  def tokenizer: StringSegmenter = members.head.tokenizer
  def lemmatizer: Lemmatizer = members.head.lemmatizer
  def containsLemmatizedWord(word: String): Boolean = members.exists(_.containsLemmatizedWord(word))
  def containsLemmatizedWords(word: Seq[String]): Boolean = members.exists(_.containsLemmatizedWords(word))
  def contains[T<:Observation[T]](query: T): Boolean = members.exists(_.contains(query))
  def contains[T<:Observation[T]](query: Seq[T]): Boolean = members.exists(_.contains(query))
  def +=(s:String): Unit = {throw new Error("method not implemented for UnionLexicon")}
  override def toString: String = {
    var st = "UNION { "
    members.foreach(st += _.toString()+" , ")
    st += " } "
    st
  }
}

