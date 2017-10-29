package cc.factorie.app.nlp.lexicon

import cc.factorie.app.nlp.lemma.LowercaseLemmatizer
import cc.factorie.app.strings.nonWhitespaceClassesSegmenter

object PersonPronoun extends PhraseLexicon("PersonPronoun", nonWhitespaceClassesSegmenter, LowercaseLemmatizer) {
  this ++=
    """anybody
anyone
everybody
everyone
he
her
hers
herself
him
himself
his
I
me
mine
myself
nobody
ours
ourselves
she
somebody
someone
theirs
them
themselves
they
us
we
who
whoever
whom
whomever
whose
you
yours
yourself
yourselves"""
}
