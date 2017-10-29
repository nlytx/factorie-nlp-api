package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.Token

object TokenFreqs{
  def countWordTypes(nonPronouns: Seq[Mention],specificWordFunc: (Mention) => String, cutoff: Int): DefaultHashMap[String,Int] = {
    countAndPrune(nonPronouns.map(specificWordFunc),cutoff)
  }

  private def countAndPrune(words: Seq[String], cutoff: Int): DefaultHashMap[String,Int] = {
    val counts = new DefaultHashMap[String,Int](0)
    words.foreach(key=>counts(key) += 1)
    counts.foreach{case (key,value) => if(value < cutoff) counts.remove(key)}
    counts
  }

  def getTokenAtOffset(token: Token,offset: Int): Token = { val t = token.next(offset); if (t ne null) t else null }
  def getTokenStringAtOffset(token: Token,offset: Int): String = { val t = token.next(offset); if (t ne null) t.string else ""}

  def getWordClass(word: Token):String = {
    val sb = new StringBuilder
    if (word.isCapitalized) {
      if (word.containsLowerCase) sb.append("Cap-Mix")
      else sb.append("Cap")
    }
    if (word.isDigits) sb.append("Num")
    else if (word.containsDigit) sb.append("Num-Mix")
    if (word.string.contains('-')) sb.append("Dash")
    if (word.string.contains('s') && word.string.length() >= 3) sb.append("-S")
    else if (word.string.length() >= 5){
      val lowerCase = word.string.toLowerCase
      if (lowerCase.endsWith("ed")) sb.append("-ed")
      else if (lowerCase.endsWith("ing")) sb.append("-ing")
      else if (lowerCase.endsWith("ion")) sb.append("-ion")
      else if (lowerCase.endsWith("er")) sb.append("-er")
      else if (lowerCase.endsWith("est")) sb.append("-est")
      else if (lowerCase.endsWith("ly")) sb.append("-ly")
      else if (lowerCase.endsWith("ity")) sb.append("-ity")
      else if (lowerCase.endsWith("y")) sb.append("-y")
      else sb.append("-none")
    }
    sb.toString()
  }
}
