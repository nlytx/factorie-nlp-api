package cc.factorie.app.nlp.ner

import java.io.Serializable

import cc.factorie.util.ModelProvider

class NoEmbeddingsConllStackedChainNer()(implicit mp:ModelProvider[NoEmbeddingsConllStackedChainNer], nerLexiconFeatures:NerLexiconFeatures) extends ConllStackedChainNer(null, 0, 0.0, false)(mp, nerLexiconFeatures) with Serializable
object NoEmbeddingsConllStackedChainNer extends NoEmbeddingsConllStackedChainNer()(ModelProvider.classpath(), StaticLexiconFeatures()) with Serializable

