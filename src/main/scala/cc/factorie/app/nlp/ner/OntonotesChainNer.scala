package cc.factorie.app.nlp.ner
/*
class OntonotesChainNer()(implicit mp:ModelProvider[OntonotesChainNer], nerLexiconFeatures:NerLexiconFeatures)
  extends ChainNer[BilouOntonotesNerTag](BilouOntonotesNerDomain, (t, s) => new BilouOntonotesNerTag(t, s), l => l.token, mp.provide, nerLexiconFeatures) {
  def newBuffer = new OntonotesNerSpanBuffer()

  def newSpan(sec: Section, start: Int, length: Int, category: String) = new OntonotesNerSpan(sec, start, length, category)
}

object OntonotesChainNer extends OntonotesChainNer()(ModelProvider.classpath(), StaticLexiconFeatures())
*/