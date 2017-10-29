package cc.factorie.app.nlp.phrase

/** A collection of Phrases that are noun phrases.  Typically used as an attribute of a Section or a Document. */
class NounPhraseList(phrases:Iterable[Phrase]) extends PhraseList(phrases)
