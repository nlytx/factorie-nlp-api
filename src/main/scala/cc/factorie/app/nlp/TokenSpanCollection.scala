package cc.factorie.app.nlp

import cc.factorie.variable.SpanVarCollection

trait TokenSpanCollection[S<:TokenSpan] extends SpanVarCollection[S, Section, Token]