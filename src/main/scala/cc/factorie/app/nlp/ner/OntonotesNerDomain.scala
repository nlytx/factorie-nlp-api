package cc.factorie.app.nlp.ner

import cc.factorie.variable.EnumDomain

object OntonotesNerDomain extends EnumDomain {
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
  WORK_OF_ART = Value
  freeze()
}
