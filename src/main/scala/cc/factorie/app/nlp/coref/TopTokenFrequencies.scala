package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.Token

class TopTokenFrequencies(val headWords: DefaultHashMap[String,Int],
                          val firstWords: DefaultHashMap[String,Int] = null,
                          val lastWords: DefaultHashMap[String,Int] = null,
                          val precContext: DefaultHashMap[String,Int] = null,
                          val followContext: DefaultHashMap[String,Int] = null,
                          val shapes: DefaultHashMap[String,Int] = null,
                          val wordForm: DefaultHashMap[String,Int] = null, default: Int = 20) {
  def this(nonPronouns: Seq[Mention],typesOfCounts: Seq[String], default:Int) = this(
    if(typesOfCounts.contains("Head")) TokenFreqs.countWordTypes(nonPronouns,(t) => t.phrase.headToken.string.toLowerCase,default) else null,
    if(typesOfCounts.contains("First")) TokenFreqs.countWordTypes(nonPronouns,(t) => t.phrase.tokens(0).string.toLowerCase,default)else null,
    if(typesOfCounts.contains("Last")) TokenFreqs.countWordTypes(nonPronouns,(t) => t.phrase.last.string.toLowerCase,default)else null,
    if(typesOfCounts.contains("Prec")) TokenFreqs.countWordTypes(nonPronouns,(t) => TokenFreqs.getTokenStringAtOffset(t.phrase.tokens(0),-1).toLowerCase,default)else null,
    if(typesOfCounts.contains("Follow")) TokenFreqs.countWordTypes(nonPronouns,(t) => TokenFreqs.getTokenStringAtOffset(t.phrase.last,1).toLowerCase,default)else null,
    if(typesOfCounts.contains("Shape")) TokenFreqs.countWordTypes(nonPronouns,(t) => cc.factorie.app.strings.stringShape(t.phrase.string,2),default)else null,
    if(typesOfCounts.contains("WordForm")) TokenFreqs.countWordTypes(nonPronouns,(t) => TokenFreqs.getWordClass(t.phrase.headToken),default)else null)


  //If this token is not a top token, fall back on using pos tag
  def containsToken(lexicon: DefaultHashMap[String,Int],token: Token): String = {
    if(lexicon.contains(token.string.toLowerCase)) token.string.toLowerCase
    else token.posTag.categoryValue
  }

  def containsString(lexicon: DefaultHashMap[String,Int],tokenString: String): String = if(lexicon.contains(tokenString)) tokenString else ""
}
