package cc.factorie.app.nlp

/** A Map from annotation class to DocumentAnnotator that provides that annotation.
    *Used to store default ways of getting certain prerequisite annotations. */
class MutableDocumentAnnotatorMap extends collection.mutable.LinkedHashMap[Class[_], () => DocumentAnnotator] {
  def +=(annotator: DocumentAnnotator) = annotator.postAttrs.foreach(a => this(a) = () => annotator)
}
