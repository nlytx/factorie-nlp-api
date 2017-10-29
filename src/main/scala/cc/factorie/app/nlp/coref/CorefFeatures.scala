package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.lexicon.StaticLexicons
import cc.factorie.app.nlp.ner.OntonotesEntityTypeDomain
import cc.factorie.app.nlp.phrase.{Gender, GenderDomain}

import scala.collection.mutable

// TODO I think this should be renamed, but I'm not sure to what. -akm
object CorefFeatures {
  val posTagsSet = Set("PRP", "PRP$", "WP", "WP$")
  val properSet = Set("NNP", "NNPS")
  val nounSet = Seq("NN", "NNS")
  val posSet = Seq("POS")

  trait Ternary
  case object True extends Ternary
  case object False extends Ternary
  case object Unknown extends Ternary

  def proWordHead(mention1: Mention,mention2: Mention): String = {
    val m1c = mention1.attr[MentionCharacteristics]
    val m2c = mention2.attr[MentionCharacteristics]
    val e1 = if (m2c.isPRO) mention2.phrase.headToken.string else m2c.predictEntityType
    val e2 = if (m1c.isPRO) mention1.phrase.headToken.string else m1c.predictEntityType
    e1 + "&&" + e2
  }

  def entityTypeMatch(mention1: Mention,mention2: Mention): Ternary = {
    val m1c = mention1.attr[MentionCharacteristics]
    val m2c = mention2.attr[MentionCharacteristics]
    if (m2c.predictEntityType == OntonotesEntityTypeDomain.O || m1c.predictEntityType == OntonotesEntityTypeDomain.O) Unknown
    else if (m2c.predictEntityType == m1c.predictEntityType) True
    else False
  }

  def acronymMatch(mention1: Mention,mention2: Mention):  Ternary = {
    val m1 = mention1.attr[MentionCharacteristics]
    val m2 = mention2.attr[MentionCharacteristics]
    if (mention1.phrase.length == 1 && mention2.phrase.length > 1) {
      if (m2.acronym.contains(mention1.phrase.string.trim.toLowerCase)) True else False
    } else if (mention1.phrase.length > 1 && mention2.phrase.length == 1) {
      if (m1.acronym.contains(mention2.phrase.string.trim.toLowerCase)) True else False
    } else Unknown
  }

  def getPairRelations(s1: Mention, s2: Mention): String = {
    val l1 = s1.phrase.headToken.string.toLowerCase
    val l2 = s2.phrase.headToken.string.toLowerCase
    val s1c = s1.attr[MentionCharacteristics]
    val s2c = s2.attr[MentionCharacteristics]
    if (l1 == l2)
      "match"
    else if (l1.contains(l2) || l2.contains(l1))
      "substring"
    else if (s1c.wnSynsets.exists(a => s2c.wnSynsets.contains(a)))
      "Syn"
    else if (s1c.wnSynsets.exists(a => s2c.wnHypernyms.contains(a)) || s2c.wnSynsets.exists(a => s1c.wnHypernyms.contains(a)))
      "Hyp"
    else if (s1c.wnSynsets.exists(s2c.wnAntonyms.contains))
      "Ant"
    else
      "Mismatch"
  }

  def matchingTokensRelations(m1:Mention, m2:Mention, lexicon:StaticLexicons) = {
    val set = new mutable.HashSet[String]()
    val m1c = m1.attr[MentionCharacteristics]
    val m2c = m2.attr[MentionCharacteristics]
    for (w1 <- m2.phrase.toSeq.map(_.string.toLowerCase))
      for (w2 <- m1.phrase.toSeq.map(_.string.toLowerCase))
       if (w1.equals(w2) || m2c.wnSynsets.exists(m1c.wnHypernyms.contains) || m1c.wnHypernyms.exists(m2c.wnHypernyms.contains) ||
           lexicon.iesl.Country.contains(w1) && lexicon.iesl.Country.contains(w2) ||
           lexicon.iesl.City.contains(w1) && lexicon.iesl.City.contains(w2) ||
           lexicon.uscensus.PersonFirstMale.contains(w1) && lexicon.uscensus.PersonFirstMale.contains(w2) ||
           // commented out the femaleFirstNames part, Roth publication did not use
           lexicon.uscensus.PersonFirstFemale.contains(w1) && lexicon.uscensus.PersonFirstFemale.contains(w2) ||
           lexicon.uscensus.PersonLast.contains(w1) && lexicon.uscensus.PersonLast.contains(w2))
        set += getPairRelations(m1, m2)
    set.toSet
  }

  def countCompatibleMentionsBetween(m1:Mention, m2:Mention, mentions:Seq[Mention]): Seq[String] = {
    val ments = mentions.filter(s => s.phrase.start < m1.phrase.start && s.phrase.start > m2.phrase.end)
    val iter = ments.iterator
    var numMatches = 0
    while (numMatches <= 2 && iter.hasNext) {
      val m = iter.next()
      if (CorefFeatures.gendersMatch(m, m1) == True && CorefFeatures.numbersMatch(m, m1) == True) numMatches += 1
    }
    if (numMatches <= 2) (0 to numMatches).map(_.toString)
    else (0 to numMatches).map(_.toString) :+ "_OVER2"
  }

  val maleHonors = Set("mr", "mister")
  val femaleHonors = Set("ms", "mrs", "miss", "misses")
  val neuterWN = Set("artifact", "location", "group")


  def strongerOf(g1: Int, g2: Int): Int = {
    if ((g1 == GenderDomain.MALE || g1 == GenderDomain.FEMALE) && (g2 == GenderDomain.PERSON || g2 == GenderDomain.UNKNOWN))
      g1
    else if ((g2 == GenderDomain.MALE || g2 == GenderDomain.FEMALE) && (g1 == GenderDomain.PERSON || g1 == GenderDomain.UNKNOWN))
      g2
    else if ((g1 == GenderDomain.NEUTER || g1 == GenderDomain.PERSON) && g2 == GenderDomain.UNKNOWN)
      g1
    else if ((g2 == GenderDomain.NEUTER || g2 == GenderDomain.PERSON) && g1 == GenderDomain.UNKNOWN)
      g2
    else
      g2
  }

  def gendersMatch(m1:Mention, m2:Mention): Ternary = {
    val g1 = m2.phrase.attr[Gender].intValue
    val g2 = m1.phrase.attr[Gender].intValue
    // TODO This condition could be simplified
    if (g1 == GenderDomain.UNKNOWN || g2 == GenderDomain.UNKNOWN)
      Unknown
    else if (g1 == GenderDomain.PERSON && (g2 == GenderDomain.MALE || g2 == GenderDomain.FEMALE || g2 == GenderDomain.PERSON))
      Unknown
    else if (g2 == GenderDomain.PERSON && (g1 == GenderDomain.MALE || g1 == GenderDomain.FEMALE || g1 == GenderDomain.PERSON))
      Unknown
    else if (g1 == g2)
      True
    else
      False
  }

  def headWordsCross(m1:Mention, m2:Mention, model: CorefModel): String = {
    val w1 = m2.attr[MentionCharacteristics].headPhraseTrim
    val w2 = m1.attr[MentionCharacteristics].headPhraseTrim
    val rare1 = 1.0 / model.CorefTokenFrequencies.counter.headWords.getOrElse(w1.toLowerCase, 1).toFloat > 0.1
    val rare2 = 1.0 / model.CorefTokenFrequencies.counter.headWords.getOrElse(w2.toLowerCase, 1).toFloat > 0.1
    if (rare1 && rare2 && w1.equalsIgnoreCase(w2))
      "Rare_Duplicate"
    else
      (if (rare1) m1.attr[MentionCharacteristics].headPos else w1) + "_AND_" + (if (rare2) m1.attr[MentionCharacteristics].headPos else w2)
  }

  val singDet = Set("a ", "an ", "this ")
  val pluDet = Set("those ", "these ", "some ", "both ")

  def numbersMatch(m1:Mention, m2:Mention): Ternary = {
    val n1 = m2.phrase.attr[Number].intValue
    val n2 = m1.phrase.attr[Number].intValue
    import cc.factorie.app.nlp.phrase.NumberDomain._
    if (n1 == n2 && n1 != UNKNOWN) True
    else if (n1 != n2 && n1 != UNKNOWN && n2 != UNKNOWN) False
    else if (n1 == UNKNOWN || n2 == UNKNOWN) {
      if (m1.phrase.toSeq.map(t => t.string.trim).mkString(" ").equals(m2.phrase.toSeq.map(t => t.string.trim).mkString(" ")))
        True
      else Unknown
    }
    else Unknown
  }

  val relativizers = Set("who", "whom", "which", "whose", "whoever", "whomever", "whatever", "whichever", "that")

  def areAppositive(m1:Mention, m2:Mention): Boolean = {
    (m2.attr[MentionCharacteristics].isProper || m1.attr[MentionCharacteristics].isProper) &&
      (m2.phrase.last.next(2) == m1.phrase.head && m2.phrase.last.next.string.equals(",") ||
        m1.phrase.last.next(2) == m2.phrase.head && m1.phrase.last.next.string.equals(","))
  }

  def isRelativeFor(m1:Mention, m2:Mention) =
    relativizers.contains(m1.attr[MentionCharacteristics].lowerCaseHead) &&
      (m2.phrase.head == m1.phrase.last.next ||
        (m2.phrase.head == m1.phrase.last.next(2) && m1.phrase.last.next.string.equals(",")
          || m2.phrase.head == m1.phrase.last.next(2) && m1.phrase.last.next.string.equals(",")))


  def areRelative(m1: Mention, m2: Mention): Boolean = isRelativeFor(m1, m2) || isRelativeFor(m2, m1)

  def canBeAliases(m1: Mention, m2: Mention): Boolean = {
    val m1c = m1.attr[MentionCharacteristics]
    val m2c = m2.attr[MentionCharacteristics]
    val eType1 = m2c.predictEntityType
    val eType2 = m1c.predictEntityType

    val m1head = m2c.lowerCaseHead
    val m2head = m1c.lowerCaseHead
    val m1Words = m1.phrase.tokens.map(_.string)
    val m2Words = m2.phrase.tokens.map(_.string)

    if (m2c.isProper && m1c.isProper && m2c.predictEntityType.equals(m1c.predictEntityType) && (m2c.predictEntityType.equals(OntonotesEntityTypeDomain.PERSON) || m2c.predictEntityType.equals(OntonotesEntityTypeDomain.GPE)))
      return m2.phrase.last.string.toLowerCase equals m1.phrase.last.string.toLowerCase

    else if ((eType1.equals(OntonotesEntityTypeDomain.ORG) || eType1.equals(OntonotesEntityTypeDomain.O)) && (eType2.equals(OntonotesEntityTypeDomain.ORG) || eType2.equals(OntonotesEntityTypeDomain.O))) {
      val (initials, shorter) =
        if (m1Words.length < m2Words.length)
          (m2c.initials, m1head)
        else
          (m1c.initials, m2head)
      return shorter.replaceAll("[., ]", "") equalsIgnoreCase initials
    }
    false
  }

  lazy val punct = "^['\"(),;.`]*(.*?)['\"(),;.`]*$".r
  def removePunct(s: String): String = {
    val punct(ret) = s
    ret
  }
}
