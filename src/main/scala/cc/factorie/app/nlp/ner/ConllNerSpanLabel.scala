package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.TokenSpan

class ConllNerSpanLabel(span:TokenSpan, initialCategory:String) extends NerSpanLabel(span, initialCategory) with Serializable { def domain = ConllNerDomain }

