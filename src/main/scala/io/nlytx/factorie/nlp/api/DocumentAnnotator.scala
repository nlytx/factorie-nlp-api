package io.nlytx.factorie.nlp.api

import cc.factorie.app.nlp.{DocumentAnnotatorPipeline, parse, pos, ner}

/**
  * Created by andrew@andrewresearch.net on 24/10/17.
  */
object DocumentAnnotator {
  val default = DocumentAnnotatorPipeline(pos.OntonotesForwardPosTagger, parse.WSJTransitionBasedParser,ner.OntonotesChainNer)

  //lemma.WordNetLemmatizer
  //coref.NerStructuredCoref
  //phrase.PosBasedNounPhraseFinder
}
