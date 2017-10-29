package cc.factorie.app.nlp.load

import cc.factorie.app.nlp.Document

/** The interface common to objects that create Documents from the files in a directory.
 *
    *@author Andrew McCallum */
trait LoadDirectory {
  def fromDirectory(dir:java.io.File): Seq[Document]
}