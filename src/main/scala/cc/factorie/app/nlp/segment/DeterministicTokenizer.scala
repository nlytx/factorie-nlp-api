package cc.factorie.app.nlp.segment

/* This version does not perform normalization, only tokenization */

object DeterministicTokenizer extends DeterministicLexerTokenizer(
  tokenizeSgml = false,
  tokenizeNewline = false,
  tokenizeWhitespace = false,
  tokenizeAllDashedWords = false,
  abbrevPrecedesLowercase = false,
  normalize = false,
  normalizeQuote = false,
  normalizeApostrophe = false,
  normalizeCurrency = false,
  normalizeAmpersand = false,
  normalizeFractions = false,
  normalizeEllipsis = false,
  undoPennParens = false,
  unescapeSlash = false,
  unescapeAsterisk = false,
  normalizeMDash = false,
  normalizeDash = false,
  normalizeHtmlSymbol = false,
  normalizeHtmlAccent = false
)
