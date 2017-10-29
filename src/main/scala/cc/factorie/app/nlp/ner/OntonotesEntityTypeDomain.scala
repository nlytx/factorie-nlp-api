package cc.factorie.app.nlp.ner

import cc.factorie.variable.EnumDomain

/** Entity types used in coreference.
  *
  * @author Andrew McCallum */
object OntonotesEntityTypeDomain extends EnumDomain {
  val O,
  CARDINAL,
  DATE,
  EVENT,
  FAC,
  GPE,
  LANGUAGE,
  LAW,
  LOC,
  MONEY,
  NORP,
  ORDINAL,
  ORG,
  PERCENT,
  PERSON,
  PRODUCT,
  QUANTITY,
  TIME,
  WORK_OF_ART,
  MISC = Value
  freeze()
}