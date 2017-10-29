package cc.factorie.app.nlp.segment

import cc.factorie.app.nlp.Token

object PlainTokenNormalizer extends TokenNormalizer1((t:Token, s:String) => new PlainNormalizedTokenString(t,s))