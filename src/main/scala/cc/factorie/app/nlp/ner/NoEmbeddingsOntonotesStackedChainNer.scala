package cc.factorie.app.nlp.ner

import java.io.Serializable

import cc.factorie.util.ModelProvider

class NoEmbeddingsOntonotesStackedChainNer()(implicit mp:ModelProvider[NoEmbeddingsOntonotesStackedChainNer], nerLexiconFeatures: NerLexiconFeatures) extends OntonotesStackedChainNer(null, 0, 0.0, false)(mp, nerLexiconFeatures) with Serializable
object NoEmbeddingsOntonotesStackedChainNer extends NoEmbeddingsOntonotesStackedChainNer()(ModelProvider.classpath(), StaticLexiconFeatures()) with Serializable