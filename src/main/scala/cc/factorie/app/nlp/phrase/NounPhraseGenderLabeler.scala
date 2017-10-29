package cc.factorie.app.nlp.phrase

/** Gender label all phrases in the Document's NounPhraseList. */
class NounPhraseGenderLabeler extends PhraseGenderLabeler[NounPhraseList](phrase=>phrase)
object NounPhraseGenderLabeler extends NounPhraseGenderLabeler
