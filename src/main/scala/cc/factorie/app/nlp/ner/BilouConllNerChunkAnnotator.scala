package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Section

object BilouConllNerChunkAnnotator extends NerChunkAnnotator[ConllNerSpan, BilouConllNerTag]({() => new ConllNerSpanBuffer}, {(s:Section, start:Int, end:Int, cat:String) => new ConllNerSpan(s, start, end, cat)})
