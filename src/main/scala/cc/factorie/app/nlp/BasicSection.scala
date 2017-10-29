package cc.factorie.app.nlp

/**
  * Created by andrew@andrewresearch.net on 27/10/17.
  */

/** A simple concrete implementation of Section. */
class BasicSection(val document:Document, val stringStart:Int, val stringEnd:Int) extends Section
