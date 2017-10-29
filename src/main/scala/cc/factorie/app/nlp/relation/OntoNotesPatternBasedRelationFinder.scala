package cc.factorie.app.nlp.relation

/**
  * Created by andrew@andrewresearch.net on 28/10/17.
  */

object OntoNotesPatternBasedRelationFinder extends PatternBasedRelationFinder(PatternRelationPredictor.predictorsFromStreams(getClass.getResourceAsStream("/cc/factorie/app/nlp/relation/patterns.tuned"), getClass.getResourceAsStream("/cc/factorie/app/nlp/relation/argtypes_ontonotes")))

