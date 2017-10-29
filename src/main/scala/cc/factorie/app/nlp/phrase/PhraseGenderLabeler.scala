package cc.factorie.app.nlp.phrase

import cc.factorie.app.nlp.coref.PronounSets
import cc.factorie.app.nlp.lexicon.{LexiconsProvider, StaticLexicons}
import cc.factorie.app.nlp.{Document, DocumentAnnotator, Token}

import scala.reflect.ClassTag

/** Cheap gender predictor based on rules and lexicon. */
class PhraseGenderLabeler[A<:AnyRef](documentAttrToPhrases:(A)=>Iterable[Phrase])(implicit docAttrClass:ClassTag[A]) extends DocumentAnnotator {

  // todo fix this
  @deprecated("This exists to preserve prior behavior, it should be a constructor argument", "10/5/15")
  val lexicon = new StaticLexicons()(LexiconsProvider.classpath())

  def process(document:Document): Document = {
    for (phrase <- documentAttrToPhrases(document.attr[A])) process(phrase)
    document
  }
  def process(phrase:Phrase): Unit = {
    import GenderDomain._
    val gender = new PhraseGender(phrase, UNKNOWN)
    phrase.attr += gender
    if (phrase.length > 0) {
      val genderFromLexicon = lexiconGender(phrase)
      if (genderFromLexicon.isDefined) gender := genderFromLexicon.get
      else {
        val firstWord = phrase(0).string.toLowerCase
        val lastWord = phrase.last.string.toLowerCase
        var firstName = firstWord
        if (lexicon.iesl.PersonHonorific.containsWord(firstWord)) {
          gender := PERSON
          if (maleHonors.contains(firstWord)) gender := MALE
          else if (femaleHonors.contains(firstWord)) gender := FEMALE
          if (phrase.length >= 3) firstName = phrase(1).string.toLowerCase
        }
        if (gender.intValue != MALE && gender.intValue != FEMALE) {
          if (lexicon.iesl.Month.containsWord(firstWord)) gender := NEUTER
          else if (lexicon.uscensus.PersonFirstMale.containsWord(firstName)) gender := MALE
          else if (lexicon.uscensus.PersonFirstFemale.containsWord(firstName) && firstName != "an") gender := FEMALE
          else if (gender.intValue == GenderDomain.UNKNOWN && lexicon.iesl.PersonLast.containsWord(lastWord)) gender := PERSON
          if (lexicon.iesl.City.contains(phrase) || lexicon.iesl.Country.contains(phrase) || lexicon.iesl.OrgSuffix.containsWord(lastWord))
            if (gender.intValue == UNKNOWN) gender := NEUTER else gender := UNKNOWN // Could be either person or other; mark it unknown
        }
      }
    }
  }

  /** Test various words in the phrase to see if they indicate gender.  Return an index into the NounPhraseGenderDomain. */
  def lexiconGender(phrase: Phrase): Option[Int] = {
    if (phrase.length == 1) lexiconGender(phrase.tokens(0).string)
    else if (phrase.length == 2) lexiconGender(phrase.tokens(0).string).orElse(lexiconGender(phrase.tokens(1).string))
    else lexiconGender(phrase.headToken.string).orElse(lexiconGender(phrase.tokens(0).string).orElse(lexiconGender(phrase.tokens(1).string)))
  }
  def lexiconGender(word:String): Option[Int] = {
    val lemma = word.toLowerCase
    if (maleWords.contains(lemma)) Some(GenderDomain.MALE)
    else if (femaleWords.contains(lemma)) Some(GenderDomain.FEMALE)
    else if (PronounSets.neuter.contains(lemma)) Some(GenderDomain.NEUTER)
    else if (PronounSets.allPersonPronouns.contains(lemma)) Some(GenderDomain.PERSON)
    else None
  }

  //since lemmaString is singular, we don't need to hard code in the plural form of these words
  val maleHonors = Set("mr.", "mr", "mister")
  val femaleHonors = Set("ms.", "ms", "mrs.", "mrs", "miss", "misses")

  val maleFemaleWords = Seq(
    ("", "actress"),
    ("", "adulteress"),
    ("", "giantess"),
    ("", "heiress"),
    ("", "hostess"),
    ("", "poetess"),
    ("", "shepherdess"),
    ("baron", "baroness"),
    ("boar", "sow"),
    ("boy", "girl"),
    ("boy-friend", "girl-friend"),
    ("boyfriend", "girlfriend"),
    ("bridegroom", "bride"),
    ("bro", "sis"),
    ("brother", "sister"),
    ("brother-in-law", "sister-in-law"),
    ("buck", "roe"),
    ("bull", "cow"),
    ("chap", ""),
    ("cock", "hen"),
    ("codger", ""),
    ("count", "countess"),
    ("dad", "mom"),
    ("dad", "mum"),
    ("daddy", "mommy"),
    ("deacon", "deaconess"),
    ("dude", "dame"),
    ("duke", "duchess"),
    ("emperor", "empress"),
    ("father", "mother"),
    ("father-in-law", "mother-in-law"),
    ("fiance", "fiancee"),
    ("fianc\u00E9", "fianc\u00E9e"),
    ("gigolo", "prostitute"),
    ("godfather", "godmother"),
    ("godson", "goddaughter"),
    ("grandfather", "grandmother"),
    ("grandpa", "grandma"),
    ("grandson", "granddaughter"),
    ("guy", "gal"),
    ("he", "she"),
    ("hero", "heroine"),
    ("him", "her"),
    ("his", "hers"),
    ("husband", "wife"),
    ("king", "queen"),
    ("lad", "lass"),
    ("landlord", "landlady"),
    ("lion", "lioness"),
    ("lord", "lady"),
    ("male", "female"),
    ("man", "woman"),
    ("manservant", "maidservant"),
    ("master", "mistress"),
    ("men", "women"),
    ("monk", "nun"),
    ("nephew", "niece"),
    ("pa", "ma"),
    ("papa", "mama"),
    ("papa", "mamma"),
    ("papa", "momma"),
    ("peacock", "peahen"),
    ("pop", "mom"),
    ("pope", ""),
    ("priest", "priestess"),
    ("prince", "princess"),
    ("ram", "ewe"),
    ("sir", "madam"),
    ("sir", "ma'am"),
    ("son-in-law", "daughter-in-law"),
    ("stallion", "mare"),
    ("step-father", "step-mother"),
    ("step-son", "step-daughter"),
    ("steward", "stewardess"),
    ("tiger", "tigress"),
    ("tom", "tib"), // cat or elephant
    ("uncle", "aunt"),
    ("waiter", "waitress"),
    ("widower", "widow")
  )
  val maleWords = maleFemaleWords.map(_._1).filter(_.length > 0).toSet
  val femaleWords = maleFemaleWords.map(_._2).filter(_.length > 0).toSet

  override def tokenAnnotationString(token:Token): String = { val phrases = documentAttrToPhrases(token.document.attr[A]).filter(_.contains(token)); phrases.map(_.attr[Gender].categoryValue).mkString(",") }
  override def phraseAnnotationString(phrase:Phrase): String = { val t = phrase.attr[Gender]; if (t ne null) t.categoryValue else "_" }
  def prereqAttrs: Iterable[Class[_]] = List(docAttrClass.runtimeClass)//Require some TokenSpanList containing subclass of Phrase elements
  // Note that this postAttr doesn't indicate if all Phrases or just some Mention Phrases were actually labeled.
  def postAttrs: Iterable[Class[_]] = List(classOf[PhraseGender])
}
