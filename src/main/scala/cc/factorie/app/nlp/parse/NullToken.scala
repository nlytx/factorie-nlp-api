package cc.factorie.app.nlp.parse

import cc.factorie.app.nlp.Token

object NullToken extends LightweightParseToken(null.asInstanceOf[Token]){
  override lazy val string = ParserConstants.NULL_STRING
  override lazy val lemmaLower = ParserConstants.NULL_STRING
  override lazy val posTagString = ParserConstants.NULL_STRING
}
