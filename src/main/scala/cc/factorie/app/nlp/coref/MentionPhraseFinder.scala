package cc.factorie.app.nlp.coref

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.phrase.Phrase

/** Trait for objects that return a list of Phrases given a Document
    *whose annotations includes those classes listed in prereqAttrs.
    *This is not a DocumentAnnotator because it does not add its results to the Document.attr;
    *invocations to its apply method simple return a collection of Phrases.
 **
 This design was chosen because these phrases are often used for coreference
    *in which there are many coreference-specific choices of what mentions are filtered
    *or included, and we didn't want to pollute the Document.attr with a tremendous number
    *of postAttrs that are specific to individual coreference solutions.
 **
 If you really want a DocumentAnnotator that saves its results, it is easy to
    *create one uses a PhraseFinder.
 **
 @author Andrew McCallum
  */
trait MentionPhraseFinder {
  def prereqAttrs: Seq[Class[_]]
  //def phrasePostAttrs: Seq[Class[_]] // TODO Should we have something like this?
  def apply(document:Document): Seq[Phrase]
}
