package cc.factorie.app.nlp.coref

class ForwardCoref extends ForwardCorefBase {
  val model = new BaseCorefModel
}

object ForwardCoref extends ForwardCoref
