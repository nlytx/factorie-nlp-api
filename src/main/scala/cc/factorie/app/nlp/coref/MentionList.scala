package cc.factorie.app.nlp.coref

import cc.factorie.util.ImmutableArrayIndexedSeq

/** An immutable ordered collection of Mentions. */
class MentionList(mentions:Iterable[Mention]) extends ImmutableArrayIndexedSeq(mentions) with MentionCollection

