package cc.factorie.app.nlp.lexicon

import cc.factorie.app.nlp.lemma.LowercaseLemmatizer
import cc.factorie.app.strings.nonWhitespaceClassesSegmenter

object PosessiveDeterminer extends PhraseLexicon("PosessiveDeterminer", nonWhitespaceClassesSegmenter, LowercaseLemmatizer) {
  this ++=
    """my
your
his
her
its
their"""
}
