package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.TokenSpanBuffer

trait NerSpanBuffer[Tag <: NerSpan] extends TokenSpanBuffer[Tag]

