package dev.davidv.translator

data class AggregatedWord(
  val word: String,
  val posGlosses: List<PosGlosses>,
  val hyphenation: List<String>?,
  val formOf: List<String>?,
  val ipaSound: List<String>?,
)

data class PosGlosses(
  val pos: String,
  val glosses: List<Gloss>,
)

data class Gloss(
  val sharedPrefixCount: Int,
  val gloss: String,
  val newCategories: List<String>?,
) {
  fun getCategoryPath(previousPath: List<String>): List<String> {
    val path = mutableListOf<String>()
    if (sharedPrefixCount > 0) {
      val prefixLen = minOf(sharedPrefixCount, previousPath.size)
      path.addAll(previousPath.subList(0, prefixLen))
    }
    newCategories?.let { path.addAll(it) }
    return path
  }
}
