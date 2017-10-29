package cc.factorie.app.nlp.coref

import scala.collection.mutable.ArrayBuffer

/** An mutable ordered collection of Mentions. */
class MentionBuffer extends ArrayBuffer[Mention] with MentionCollection
