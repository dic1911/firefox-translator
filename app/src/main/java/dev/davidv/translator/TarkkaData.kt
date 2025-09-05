package dev.davidv.translator

data class AggregatedWord(
  val word: String,
  val posGlosses: List<PosGlosses>,
  val hyphenation: Array<String>?,
  val formOf: Array<String>?,
  val ipaSound: Array<String>?,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AggregatedWord

    if (word != other.word) return false
    if (posGlosses != other.posGlosses) return false
    if (hyphenation != null) {
      if (other.hyphenation == null) return false
      if (!hyphenation.contentEquals(other.hyphenation)) return false
    } else if (other.hyphenation != null) {
      return false
    }
    if (formOf != null) {
      if (other.formOf == null) return false
      if (!formOf.contentEquals(other.formOf)) return false
    } else if (other.formOf != null) {
      return false
    }
    if (ipaSound != null) {
      if (other.ipaSound == null) return false
      if (!ipaSound.contentEquals(other.ipaSound)) return false
    } else if (other.ipaSound != null) {
      return false
    }

    return true
  }

  override fun hashCode(): Int {
    var result = word.hashCode()
    result = 31 * result + posGlosses.hashCode()
    result = 31 * result + (hyphenation?.contentHashCode() ?: 0)
    result = 31 * result + (formOf?.contentHashCode() ?: 0)
    result = 31 * result + (ipaSound?.contentHashCode() ?: 0)
    return result
  }
}

data class PosGlosses(
  val pos: String,
  val glosses: List<Gloss>,
)

data class Gloss(
  val sharedPrefixCount: Int,
  val gloss: String,
  val newCategories: Array<String>?,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Gloss

    if (sharedPrefixCount != other.sharedPrefixCount) return false
    if (gloss != other.gloss) return false
    if (newCategories != null) {
      if (other.newCategories == null) return false
      if (!newCategories.contentEquals(other.newCategories)) return false
    } else if (other.newCategories != null) {
      return false
    }

    return true
  }

  override fun hashCode(): Int {
    var result = sharedPrefixCount
    result = 31 * result + gloss.hashCode()
    result = 31 * result + (newCategories?.contentHashCode() ?: 0)
    return result
  }

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
