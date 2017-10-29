package cc.factorie.app.nlp.lexicon

import cc.factorie.app.nlp.lemma.LowercaseLemmatizer
import cc.factorie.app.strings.nonWhitespaceClassesSegmenter

class CustomStopWords extends TriePhraseLexicon("CustomStopWords", nonWhitespaceClassesSegmenter, LowercaseLemmatizer) {
  def this(filename: String) = {
    this()
    this ++= scala.io.Source.fromFile(filename)
  }
  def this(words: Seq[String]) = {
    this()
    words.foreach { w => this += w }
  }
}

object CustomStopWords {
  def apply(filename: String) = new CustomStopWords(filename)
}