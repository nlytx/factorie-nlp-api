package cc.factorie.app.nlp.lemma

trait Lemmatizer {
  def lemmatize(word:String): String
}
