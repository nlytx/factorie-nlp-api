package io.nlytx.factorie.nlp.api

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.coref.MentionList

/**
  * Created by andrew@andrewresearch.net on 24/10/17.
  */

class DocumentBuilder {

  lazy val annotator = DocumentAnnotator.default

  def createAnnotatedDoc(text:String):Document = {
    val doc = new Document(text)
    annotator.process(doc)
    doc
  }

  def getDetails(doc:Document):String = {

    val owplString:String = doc.owplString(annotator.annotators.map(p => p.tokenAnnotationString(_)))
    val mentions:String = doc.attr[MentionList].map { m =>
      val phrase = m.phrase
      val mentionAnnotations = annotator.annotators.map(a => a.mentionAnnotationString(m)).mkString(", ")
      phrase + ">> " + mentionAnnotations
    }.mkString(", ")
    val docAnnotations:String = annotator.annotators.map(a => a.documentAnnotationString(doc)).mkString(", ")

    s"owpl: $owplString\n mentions: $mentions\n docAnnotations: $docAnnotations\n"
  }

}