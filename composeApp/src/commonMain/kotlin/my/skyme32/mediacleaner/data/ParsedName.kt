package my.skyme32.mediacleaner.data

data class ParsedName(
    val name: String,
    val season: Int? = null,
    val episode: Int? = null,
    val year: String? = null,
    val detectedTemporada: Int? = null
)