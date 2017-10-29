package cc.factorie.app.nlp.relation

object ConllPatternBasedRelationFinder extends PatternBasedRelationFinder(PatternRelationPredictor.predictorsFromStreams(getClass.getResourceAsStream("/cc/factorie/app/nlp/relation/patterns.tuned"), getClass.getResourceAsStream("/cc/factorie/app/nlp/relation/argtypes_conll")))
