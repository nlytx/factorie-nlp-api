package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.embeddings.SkipGramEmbedding
import cc.factorie.util.ModelProvider

class ConllStackedChainNer(embeddingMap: SkipGramEmbedding,
                           embeddingDim: Int,
                           scale: Double,
                           useOffsetEmbedding: Boolean)(implicit mp:ModelProvider[ConllStackedChainNer], nerLexiconFeatures:NerLexiconFeatures)
  extends StackedChainNer[BilouConllNerTag](
    BilouConllNerDomain,
    (t, s) => new BilouConllNerTag(t, s),
    l => l.token,
    embeddingMap,
    embeddingDim,
    scale,
    useOffsetEmbedding,
    mp.provide, nerLexiconFeatures)
