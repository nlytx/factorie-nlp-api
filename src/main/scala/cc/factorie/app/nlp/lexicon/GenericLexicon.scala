package cc.factorie.app.nlp.lexicon

import cc.factorie.util.ModelProvider

/**
  * Created by andrew@andrewresearch.net on 28/10/17.
  */

class GenericLexicon(name:String, val provider:ModelProvider[GenericLexicon]) extends TriePhraseLexicon(name) with ProvidedLexicon[GenericLexicon]