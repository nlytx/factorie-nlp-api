package cc.factorie.app.nlp

import cc.factorie.variable.SpanVarList

/** An immutable collection of TokenSpans, with various methods to returns filtered sub-sets of spans based on position and class. */
class TokenSpanList[S<:TokenSpan](spans:Iterable[S]) extends SpanVarList[S, Section, Token](spans) with TokenSpanCollection[S]
