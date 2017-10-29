package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.embeddings.SkipGramEmbedding
import cc.factorie.util.ModelProvider

class OntonotesStackedChainNer(embeddingMap: SkipGramEmbedding,
                               embeddingDim: Int,
                               scale: Double,
                               useOffsetEmbedding: Boolean)(implicit mp:ModelProvider[OntonotesStackedChainNer], nerLexiconFeatures:NerLexiconFeatures)
  extends StackedChainNer[BilouOntonotesNerTag](
    BilouOntonotesNerDomain,
    (t, s) => new BilouOntonotesNerTag(t, s),
    l => l.token,
    embeddingMap,
    embeddingDim,
    scale,
    useOffsetEmbedding,
    mp.provide, nerLexiconFeatures)
