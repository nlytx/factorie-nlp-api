package cc.factorie.app.nlp.segment

/* This version performs normalization while it tokenizes, and also includes html tags as tokens */
object DeterministicNormalizingHtmlTokenizer extends DeterministicLexerTokenizer(
  tokenizeSgml = true,
  tokenizeNewline = false,
  tokenizeWhitespace = false,
  tokenizeAllDashedWords = false,
  abbrevPrecedesLowercase = false,
  normalize = true,
  normalizeQuote = true,
  normalizeApostrophe = true,
  normalizeCurrency = true,
  normalizeAmpersand = true,
  normalizeFractions = true,
  normalizeEllipsis = true,
  undoPennParens = true,
  unescapeSlash = true,
  unescapeAsterisk = true,
  normalizeMDash = true,
  normalizeDash = true,
  normalizeHtmlSymbol = true,
  normalizeHtmlAccent = true
)
