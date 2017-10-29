package cc.factorie.app.nlp.relation

import cc.factorie.app.nlp.coref.Mention
import cc.factorie.util.Attr
import cc.factorie.variable.ArrowVariable

import scala.collection.mutable.ArrayBuffer

class RelationMention(val arg1: Mention, val arg2: Mention, var isArg1First:Boolean=true) extends ArrowVariable(arg1, arg2) with Attr {
  val _relations = ArrayBuffer[TACRelation]()
  this.attr += TACRelationList(_relations)
  def relations = this.attr[TACRelationList]
}

