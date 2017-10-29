package cc.factorie.app.nlp.coref

trait CorefTrainerOpts extends cc.factorie.util.DefaultCmdOptions {
  val trainFile = new CmdOption("train", "src/main/resources/conll-train-clean.txt", "STRING", "File with training data")
  val testFile = new CmdOption("test", "src/main/resources/conll-test-clean.txt", "STRING", "File with testing data")
  val useExactEntTypeMatch = new CmdOption("use-exact-entity-type-match", true, "BOOLEAN", "whether to require exact alignment between NER annotation and NP annotation")
  val useGoldBoundaries = new CmdOption("use-gold-boundaries",false,"BOOLEAN","whether to use gold parse boundaries + gold mention boundaries")
  val mentionAlignmentShiftWidth = new CmdOption("alignment-width",0,"INT","tolerance on boundaries when aligning detected mentions to gt mentions")
  val portion = new CmdOption("portion", 1.0, "DOUBLE", "Portion of corpus to load.")
  val useNerMentions = new CmdOption("use-ner-mentions", false, "BOOLEAN", "Whether to use NER mentions instead of noun phrase mentions")
  val randomSeed = new CmdOption("random-seed", 0, "INT", "Seed for the random number generator")
  val writeConllFormat = new CmdOption("write-conll-format", true, "BOOLEAN", "Write CoNLL format data.")
}
