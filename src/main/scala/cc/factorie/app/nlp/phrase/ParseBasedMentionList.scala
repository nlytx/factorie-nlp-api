package cc.factorie.app.nlp.phrase

import cc.factorie.app.nlp.coref.{Mention, MentionList}

class ParseBasedMentionList(spans:Iterable[Mention]) extends MentionList(spans)
