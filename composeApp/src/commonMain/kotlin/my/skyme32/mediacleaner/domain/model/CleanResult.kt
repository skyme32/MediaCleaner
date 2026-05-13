package my.skyme32.mediacleaner.domain.model

data class CleanResult(
    val cleanedName: String,
    val season: Int?,
    val episode: Int?
) {
    val isEpisode: Boolean get() = season != null && episode != null
    val mediaType: MediaType get() = if (isEpisode) MediaType.SERIES else MediaType.MOVIE
}

