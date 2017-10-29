package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Section

class OntonotesNerSpan(section:Section, start:Int, length:Int, category:String) extends NerSpan(section, start, length) with Serializable { val label = new OntonotesNerSpanLabel(this, category) }
