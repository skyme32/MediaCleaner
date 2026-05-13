package my.skyme32.mediacleaner.domain.model

enum class MediaType { SERIES, MOVIE }

data class MediaEntry(
    val id: Long,
    val originalName: String,
    val cleanedName: String,
    val mediaType: MediaType,
    val season: Int?,
    val episode: Int?,
    val timestamp: Long
)

