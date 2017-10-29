package cc.factorie.app.nlp.coref

import scala.collection.mutable

class DefaultHashMap[String,Int](val defaultValue: Int) extends mutable.HashMap[String,Int] {
  override def default(key:String) = defaultValue
}
