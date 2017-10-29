package cc.factorie.app.nlp.segment

import cc.factorie.app.nlp.Token

object OntonotesTokenNormalizer extends TokenNormalizer1((t:Token, s:String) => new OntonotesNormalizedTokenString(t,s)) {
  override def processToken(token:Token): Unit = {
    super.processToken(token)
    // TODO Add more normalization here (not yet sure what needed), but keep Lemma issues separate!
    // coexist -> co-exist
  }
}
