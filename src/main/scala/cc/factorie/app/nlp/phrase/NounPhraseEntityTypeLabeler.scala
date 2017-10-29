package cc.factorie.app.nlp.phrase

import cc.factorie.util.ClasspathURL

object NounPhraseEntityTypeLabeler extends OntonotesPhraseEntityTypeLabeler(ClasspathURL[OntonotesPhraseEntityTypeLabeler](".factorie").openConnection().getInputStream)
