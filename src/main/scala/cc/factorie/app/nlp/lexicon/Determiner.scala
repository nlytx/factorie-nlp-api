package cc.factorie.app.nlp.lexicon

import cc.factorie.app.nlp.lemma.LowercaseLemmatizer
import cc.factorie.app.strings.nonWhitespaceClassesSegmenter

object Determiner extends PhraseLexicon("Determiner", nonWhitespaceClassesSegmenter, LowercaseLemmatizer) {
  this ++=
    """the
a
this
an
that
some
all
these
no
any
those
another
both
each
every
either
neither
"""
}
