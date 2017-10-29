package cc.factorie.app.nlp

/**User: apassos
  * Date: 8/7/13
  * Time: 2:48 PM
  */

/** A sequence of DocumentAnnotators packaged as a single DocumentAnnotator.
    This class also properly populates the Document.annotators with a record of which DocumentAnnotator classes provided which annotation classes. */
class DocumentAnnotationPipeline(val annotators: Seq[DocumentAnnotator], val prereqAttrs: Seq[Class[_]] = Seq()) extends DocumentAnnotator {
  var profile = false
  var tokensProcessed = 0
  var msProcessed = 0L
  val timePerAnnotator = collection.mutable.LinkedHashMap[DocumentAnnotator,Long]()
  def postAttrs = annotators.flatMap(_.postAttrs).distinct
  def process(document: Document) = {
    var doc = document
    val t00 = System.currentTimeMillis()
    for (annotator <- annotators; if annotator.postAttrs.forall(!doc.hasAnnotation(_))) {
      val t0 = System.currentTimeMillis()
      doc = annotator.process(doc)
      if (profile) timePerAnnotator(annotator) = timePerAnnotator.getOrElse(annotator, 0L) + System.currentTimeMillis() - t0
      annotator.postAttrs.foreach(a => document.annotators(a) = annotator.getClass)
    }
    if (profile) {
      msProcessed += System.currentTimeMillis() - t00
      tokensProcessed += doc.tokenCount
    }
    doc
  }
  def profileReport: String = {
    s"Processed $tokensProcessed tokens in ${msProcessed/1000.0} seconds, at ${tokensProcessed.toDouble*1000.0/msProcessed} tokens / second " +
      "Speeds of individual components:\n" + timePerAnnotator.map(i => f"   ${i._1.getClass.getSimpleName}%30s: ${tokensProcessed.toDouble*1000.0/i._2}%4.4f tokens/sec ").mkString("\n")
  }
  def tokenAnnotationString(token: Token): String = annotators.map(_.tokenAnnotationString(token)).mkString("\t")
}
