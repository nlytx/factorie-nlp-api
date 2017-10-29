package cc.factorie.app.nlp.phrase

import cc.factorie.app.nlp.TokenSpanList

/** A collection of Phrases.  Typically used as an attribute of a Section or a Document. */
class PhraseList(spans:Iterable[Phrase]) extends TokenSpanList[Phrase](spans)
