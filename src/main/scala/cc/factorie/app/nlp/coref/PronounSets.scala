package cc.factorie.app.nlp.coref

import scala.collection.mutable

object PronounSets {
  val firstPerson = Set("i", "me", "myself", "mine", "my", "we", "us", "ourself", "ourselves", "ours", "our")
  val secondPerson = Set("you", "yourself", "yours", "your", "yourselves")
  val thirdPerson = Set("he", "him", "himself", "his", "she", "herself", "hers", "her", "it", "itself", "its", "one", "oneself", "one's", "they", "them", "themself", "themselves", "theirs", "their",  "'em")
  val other = Set("who", "whom", "whose", "where", "when", "which")

  val demonstrative = Set("this", "that", "these", "those")

  val singular = Set("i", "me", "myself", "mine", "my", "yourself", "he", "him", "himself", "his", "she", "her", "herself", "hers", "her", "it", "itself", "its", "one", "oneself", "one's")
  val plural = Set("we", "us", "ourself", "ourselves", "ours", "our", "yourself", "yourselves", "they", "them", "themself", "themselves", "theirs", "their")
  val male = Set("he", "him", "himself", "his")
  val female = Set("her", "hers", "herself", "she")

  val reflexive = Set("herself", "himself", "itself", "themselves", "yourselves", "oneself", "yourself", "themself", "myself")

  val neuter = Set("it", "its", "itself", "this", "that", "anything", "something",  "everything", "nothing", "which", "what", "whatever", "whichever")
  val personal = Set("you", "your", "yours", "i", "me", "my", "mine", "we", "our", "ours", "us", "myself", "ourselves", "themselves", "themself", "ourself", "oneself", "who", "whom", "whose", "whoever", "whomever", "anyone", "anybody", "someone", "somebody", "everyone", "everybody", "nobody")

  val allPronouns = firstPerson ++ secondPerson ++ thirdPerson ++ other
  val allPersonPronouns = allPronouns -- neuter
  val canonicalForms = new mutable.HashMap[String,String](){
    ("i", "i")
    ("i", "i")
    ("me", "i")
    ("my", "i")
    ("myself", "i")
    ("mine", "i")
    ("you", "you")
    ("your", "you")
    ("yourself", "you")
    ("yourselves", "you")
    ("yours", "you")
    ("he", "he")
    ("him", "he")
    ("his", "he")
    ("himself", "he")
    ("she", "she")
    ("her", "she")
    ("herself", "she")
    ("hers", "she")
    ("we", "we")
    ("us", "we")
    ("our", "we")
    ("ourself", "we")
    ("ourselves", "we")
    ("ours", "we")
    ("they", "they")
    ("them", "they")
    ("their", "they")
    ("themself", "they")
    ("themselves", "they")
    ("theirs", "they")
    ("'em", "they")
    ("it", "it")
    ("itself", "it")
    ("its", "it")
    ("one", "one")
    ("oneself", "one")
    ("one's", "one")
    ("this", "this")
    ("that", "that")
    ("these", "these")
    ("those", "those")
    ("which", "which")
    ("who", "who")
    ("whom", "who")
    ("thy", "thy")
    ("y'all", "you")
    ("you're", "you")
    ("you'll", "you")
    ("'s", "'s")
  }
}
