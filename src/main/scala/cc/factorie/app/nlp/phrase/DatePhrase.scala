package cc.factorie.app.nlp.phrase

import java.util.GregorianCalendar

import cc.factorie.app.nlp.Token

/**
  * Created by andrew@andrewresearch.net on 28/10/17.
  */

class DatePhrase(startToken: Token, length: Int = 1, val day: Int = -1, val month: Int = -1, val year: Int = Int.MinValue, val weekDay: Int = -1)
  extends Phrase(startToken.section, startToken.positionInSection, length, 0) {

  def toJavaDate: java.util.Date = new GregorianCalendar(year, month, day).getTime

  override def toString: String = {
    var s = ""
    if (weekDay >= 0) s += DatePhraseFinder.nrToWeekDay(weekDay) + ", "
    if (day >= 0) s += day + " "
    if (month >= 0) s += DatePhraseFinder.nrToMonth(month - 1) + " "
    if (year >= 0) s += year
    s.trim
  }

  def toLocatedDate = LocatedDate(toJavaDate, this.document.name, characterOffsets._1, characterOffsets._2)
}

