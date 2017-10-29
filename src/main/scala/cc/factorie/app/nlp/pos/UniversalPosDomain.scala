package cc.factorie.app.nlp.pos

import cc.factorie.variable.EnumDomain


/** The "A Universal Part-of-Speech Tagset"
  * by Slav Petrov, Dipanjan Das and Ryan McDonald
  * http://arxiv.org/abs/1104.2086
  * http://code.google.com/p/universal-pos-tags
  **
  *VERB - verbs (all tenses and modes)
  *NOUN - nouns (common and proper)
  *PRON - pronouns
  *ADJ - adjectives
  *ADV - adverbs
  *ADP - adpositions (prepositions and postpositions)
  *CONJ - conjunctions
  *DET - determiners
  *NUM - cardinal numbers
  *PRT - particles or other function words
  *X - other: foreign words, typos, abbreviations
  *. - punctuation
  */
object UniversalPosDomain extends EnumDomain {
  this ++= Vector("VERB", "NOUN", "PRON", "ADJ", "ADV", "ADP", "CONJ", "DET", "NUM", "PRT", "X", ".")
  freeze()
  private val Penn2universal = new scala.collection.mutable.HashMap[String,String] ++= Vector(
    "!" -> ".",
    "#" -> ".",
    "$" -> ".",
    "''" ->  ".",
    "(" -> ".",
    ")" -> ".",
    "," -> ".",
    "-LRB-" -> ".",
    "-RRB-" -> ".",
    "." -> ".",
    ":" -> ".",
    "?" -> ".",
    "CC" -> "CONJ",
    "CD" -> "NUM",
    "CD|RB" -> "X",
    "DT" -> "DET",
    "EX"-> "DET",
    "FW" -> "X",
    "IN" -> "ADP",
    "IN|RP" -> "ADP",
    "JJ" -> "ADJ",
    "JJR" -> "ADJ",
    "JJRJR" -> "ADJ",
    "JJS" -> "ADJ",
    "JJ|RB" -> "ADJ",
    "JJ|VBG" -> "ADJ",
    "LS" -> "X",
    "MD" -> "VERB",
    "NN" -> "NOUN",
    "NNP" -> "NOUN",
    "NNPS" -> "NOUN",
    "NNS" -> "NOUN",
    "NN|NNS" -> "NOUN",
    "NN|SYM" -> "NOUN",
    "NN|VBG" -> "NOUN",
    "NP" -> "NOUN",
    "PDT" -> "DET",
    "POS" -> "PRT",
    "PRP" -> "PRON",
    "PRP$" -> "PRON",
    "PRP|VBP" -> "PRON",
    "PRT" -> "PRT",
    "RB" -> "ADV",
    "RBR" -> "ADV",
    "RBS" -> "ADV",
    "RB|RP" -> "ADV",
    "RB|VBG" -> "ADV",
    "RN" -> "X",
    "RP" -> "PRT",
    "SYM" -> "X",
    "TO" -> "PRT",
    "UH" -> "X",
    "VB" -> "VERB",
    "VBD" -> "VERB",
    "VBD|VBN" -> "VERB",
    "VBG" -> "VERB",
    "VBG|NN" -> "VERB",
    "VBN" -> "VERB",
    "VBP" -> "VERB",
    "VBP|TO" -> "VERB",
    "VBZ" -> "VERB",
    "VP" -> "VERB",
    "WDT" -> "DET",
    "WH" -> "X",
    "WP" -> "PRON",
    "WP$" -> "PRON",
    "WRB" -> "ADV",
    "``" -> ".")
  def categoryFromPenn(PennPosCategory:String): String = Penn2universal(PennPosCategory)
}
