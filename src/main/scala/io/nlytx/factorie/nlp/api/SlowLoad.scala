package io.nlytx.factorie.nlp.api

import cc.factorie.app.nlp.lexicon.{LexiconsProvider, StaticLexicons}
import cc.factorie.app.nlp.ner.StaticLexiconFeatures
import cc.factorie.app.nlp.parse.OntonotesTransitionBasedParser
import cc.factorie.app.nlp.pos.OntonotesForwardPosTagger
import cc.factorie.app.nlp.{DocumentAnnotatorPipeline, coref, ner}
import cc.factorie.util.{ClasspathURL, ModelProvider}


/**
  * Created by andrew@andrewresearch.net on 24/10/17.
  */
object SlowLoad {

  private val slf = new StaticLexiconFeatures(new StaticLexicons()(LexiconsProvider.classpath()), "en")

  private val nerMp = ModelProvider.classpath[ner.ConllChainNer]()
  val nerTagger = new ner.ConllChainNer()(nerMp,slf)
  System.setProperty(
    classOf[ner.ConllChainNer].getName,
    ClasspathURL[ner.ConllChainNer](".factorie").getPath
  )

  //private val pMp = ModelProvider.classpath[OntonotesPhraseEntityTypeLabeler]()
 // private val phraseLabeler = new OntonotesPhraseEntityTypeLabeler() //(pMp)
//  System.setProperty(
//    classOf[OntonotesPhraseEntityTypeLabeler].getName,
//    ClasspathURL[OntonotesPhraseEntityTypeLabeler](".factorie").getPath
//  )

  //private val forCoref = coref.ForwardCoref

  //private val posTagger =  OntonotesForwardPosTagger
  //private val parser = OntonotesTransitionBasedParser


  //val pipeline = DocumentAnnotatorPipeline(posTagger,parser,nerTagger,forCoref)





  //BilouOntonotesNerChunkAnnotator,
  //NerForwardCoref,
  //ConllPatternBasedRelationFinder
  //lemma.WordNetLemmatizer
  //coref.NerStructuredCoref
  //phrase.PosBasedNounPhraseFinder

}
