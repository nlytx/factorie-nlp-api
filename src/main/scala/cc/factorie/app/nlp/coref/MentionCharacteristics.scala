package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.lexicon.{StaticLexicons, StopWords}
import cc.factorie.app.nlp.phrase.{Gender, NounPhraseType, Number, OntonotesPhraseEntityType}
import cc.factorie.app.nlp.wordnet.WordNet

/** Various lazily-evaluated cached characteristics of a Mention, typically attached to a Mention as an attr. */
class MentionCharacteristics(val mention: Mention, lexicon:StaticLexicons) {
  // TODO These should be cleaned up and made more efficient -akm
  lazy val isPRO = CorefFeatures.posTagsSet.contains(mention.phrase.headToken.posTag.categoryValue)
  lazy val isProper = CorefFeatures.properSet.contains(mention.phrase.headToken.posTag.categoryValue)
  lazy val isNoun = CorefFeatures.nounSet.contains(mention.phrase.headToken.posTag.categoryValue)
  lazy val isPossessive = CorefFeatures.posSet.contains(mention.phrase.headToken.posTag.categoryValue)

  lazy val hasSpeakWord = mention.phrase.exists(s => lexicon.iesl.Say.contains(s.string))
  lazy val hasSpeakWordContext = prev.exists(w => lexicon.iesl.Say.containsWord(w)) || follow.exists(w => lexicon.iesl.Say.containsWord(w))
  lazy val wnLemma = WordNet.lemma(mention.phrase.headToken.string, "n")
  lazy val wnSynsets = WordNet.synsets(wnLemma).toSet
  lazy val wnHypernyms = WordNet.hypernyms(wnLemma)
  lazy val wnAntonyms = wnSynsets.flatMap(_.antonyms()).toSet
  lazy val nounWords: Set[String] =
    mention.phrase.tokens.filter(_.posTag.categoryValue.startsWith("N")).map(t => t.string.toLowerCase).toSet
  lazy val lowerCaseHead: String = mention.phrase.headToken.string.toLowerCase
  lazy val lowerCaseString:String =  mention.phrase.string.toLowerCase
  lazy val headPhraseTrim: String = mention.phrase.tokensString(" ").trim
  lazy val nonDeterminerWords: Seq[String] =
    mention.phrase.tokens.filterNot(_.posTag.categoryValue == "DT").map(t => t.string.toLowerCase)
  lazy val initials: String =
    mention.phrase.tokens.map(_.string).filterNot(lexicon.iesl.OrgSuffix.contains).filter(t => t(0).isUpper).map(_(0)).mkString("")
  lazy val predictEntityType: Int = mention.phrase.attr[OntonotesPhraseEntityType].intValue
  lazy val demonym: String = lexicon.iesl.DemonymMap.getOrElse(headPhraseTrim, "")

  lazy val capitalization: Char = {
    if (mention.phrase.length == 1 && mention.phrase.head.positionInSentence == 0) 'u' // mention is the first word in sentence
    else {
      val s = mention.phrase.value.filter(_.posTag.categoryValue.startsWith("N")).map(_.string.trim) // TODO Fix this slow String operation
      if (s.forall(_.forall(_.isUpper))) 'a'
      else if (s.forall(t => t.head.isLetter && t.head.isUpper)) 't'
      else 'f'
    }
  }
  lazy val gender = mention.phrase.attr[Gender].categoryValue
  lazy val number = mention.phrase.attr[Number].categoryValue
  lazy val nounPhraseType = mention.phrase.attr[NounPhraseType].categoryValue
  lazy val genderIndex = mention.phrase.attr[Gender].intValue
  lazy val numberIndex = mention.phrase.attr[Number].intValue
  lazy val nounPhraseTypeIndex = mention.phrase.attr[NounPhraseType].intValue
  lazy val headPos = mention.phrase.headToken.posTag.categoryValue
  lazy val inParens = mention.phrase.sentence.tokens.exists(t => t.posTag.categoryValue == "LRB" && t.positionInSection < mention.phrase.start)
  lazy val prev = Vector(TokenFreqs.getTokenStringAtOffset(mention.phrase(0),-1), TokenFreqs.getTokenStringAtOffset(mention.phrase(0),-2))
  lazy val follow = Vector(TokenFreqs.getTokenStringAtOffset(mention.phrase.last,1), TokenFreqs.getTokenStringAtOffset(mention.phrase.last,2))

  lazy val acronym: Set[String] = {
    if (mention.phrase.length == 1)
      Set.empty
    else {
      val alt1 = mention.phrase.value.map(_.string.trim).filter(_.exists(_.isLetter)) // tokens that have at least one letter character
      val alt2 = alt1.filterNot(t => StopWords.contains(t.toLowerCase)) // alt1 tokens excluding stop words
      val alt3 = alt1.filter(_.head.isUpper) // alt1 tokens that are capitalized
      val alt4 = alt2.filter(_.head.isUpper)
      Seq(alt1, alt2, alt3, alt4).map(_.map(_.head).mkString.toLowerCase).toSet
    }
  }

  lazy val canonicalizedPronounOrType =
    if (isPRO) PronounSets.canonicalForms.getOrElse(lowerCaseString,lowerCaseHead)
    else nounPhraseType
}