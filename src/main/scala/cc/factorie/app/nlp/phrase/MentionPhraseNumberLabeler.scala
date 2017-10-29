package cc.factorie.app.nlp.phrase

import cc.factorie.app.nlp.coref.WithinDocCoref

//class MentionPhraseNumberLabeler extends PhraseNumberLabeler[WithinDocCoref](_.mentions.map(_.phrase))
object MentionPhraseNumberLabeler extends NounPhraseNumberLabeler[WithinDocCoref](_.mentions.map(_.phrase))
