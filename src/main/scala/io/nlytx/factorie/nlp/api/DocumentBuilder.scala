package io.nlytx.factorie.nlp.api

import cc.factorie.app.nlp.Document


/**
  * Created by andrew@andrewresearch.net on 24/10/17.
  */

class DocumentBuilder {

  lazy val pipeline = DocumentAnnotator.pipeline

  def createAnnotatedDoc(text:String):Document = {
    pipeline.profile = true
    val doc = new Document(text)
    pipeline.process(doc)
    println(pipeline.profileReport)
    doc
  }

//  def url[T: TypeTag] = {
//    //val prefix = "models/"
//    val suffix = ".model"
//    val name = typeOf[T].typeSymbol.fullName
//    val path = name + suffix
//    println(s"Path: $path")
//    this.getClass.getClassLoader.getResource(path)
//  }


//  def getDetails(doc:Document):String = {
//
//    val owplString:String = doc.owplString(pipeline.annotators.map(p => p.tokenAnnotationString(_)))
//    val mentions:String = doc.attr[MentionList].map { m =>
//      val phrase = m.phrase
//      val mentionAnnotations = pipeline.annotators.map(a => a.mentionAnnotationString(m)).mkString(", ")
//      phrase + ">> " + mentionAnnotations
//    }.mkString(", ")
//    val docAnnotations:String = pipeline.annotators.map(a => a.documentAnnotationString(doc)).mkString(", ")
//
//    s"owpl: $owplString\n mentions: $mentions\n docAnnotations: $docAnnotations\n"
//  }

}

// Example usages:
// token.sentence.attr[ParseTree].parent(token)
// sentence.attr[ParseTree].children(token)
// sentence.attr[ParseTree].setParent(token, parentToken)
// sentence.attr[ParseTree].label(token)
// sentence.attr[ParseTree].label(token).set("SUBJ")

// Methods also created in Token supporting:
// token.parseParent
// token.setParseParent(parentToken)
// token.parseChildren
// token.parseLabel
// token.leftChildren