package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Section

object BilouOntonotesNerChunkAnnotator extends NerChunkAnnotator[OntonotesNerSpan, BilouOntonotesNerTag]({() => new OntonotesNerSpanBuffer}, {(s:Section, start:Int, end:Int, cat:String) => new OntonotesNerSpan(s, start, end, cat)})