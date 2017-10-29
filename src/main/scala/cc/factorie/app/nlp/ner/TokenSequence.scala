package cc.factorie.app.nlp.ner

import cc.factorie.app.nlp.Token

import scala.reflect.ClassTag

class TokenSequence[T<:NerTag](token: Token)(implicit m: ClassTag[T]) extends collection.mutable.ArrayBuffer[Token] {
  this.prepend(token)
  val label : String = token.attr[T].categoryValue.split("-")(1)
  def key = this.mkString("-")
}
