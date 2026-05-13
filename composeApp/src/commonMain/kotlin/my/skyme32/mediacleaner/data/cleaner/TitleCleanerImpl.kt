package my.skyme32.mediacleaner.data.cleaner

import my.skyme32.mediacleaner.data.ParsedName
import my.skyme32.mediacleaner.domain.cleaner.TitleCleaner
import my.skyme32.mediacleaner.domain.model.CleanResult

class TitleCleanerImpl(
    private val noiseTermsProvider: () -> List<String>
) : TitleCleaner {

    override fun clean(rawName: String): CleanResult {
        val parsed = ParsedName(name = rawName)
            .let(::extractYear)
            .let(::extractSeasonEpisode)
            .let(::removeBrackets)
            .let(::removeParentheses)
            .let(::normalizeSeparators)
            .let(::detectTemporada)
            .let(::processChapterInfo)
            .let(::removeMetadataKeywords)
            .let(::applyNoiseFilter)
            .let(::cleanupRemnants)

        return buildCleanResult(parsed)
    }

    private fun extractYear(p: ParsedName): ParsedName {
        val match = YEAR_PATTERN.find(p.name) ?: return p
        return p.copy(
            name = p.name.replaceFirst(match.value, " "),
            year = match.value
        )
    }

    private fun extractSeasonEpisode(p: ParsedName): ParsedName {
        val match = SE_PATTERN.find(p.name) ?: return p
        return p.copy(
            name = p.name.substring(0, match.range.first),
            season = match.groupValues[1].toInt(),
            episode = match.groupValues[2].toInt()
        )
    }

    private fun removeBrackets(p: ParsedName): ParsedName =
        p.copy(name = BRACKET_PATTERN.replace(p.name, " "))

    private fun removeParentheses(p: ParsedName): ParsedName {
        val cleaned = PARENTHESIS_PATTERN.replace(p.name) { match ->
            val content = match.groupValues[1].trim()
            if (content.matches(YEAR_PATTERN)) " $content " else " "
        }
        return p.copy(name = cleaned)
    }

    private fun normalizeSeparators(p: ParsedName): ParsedName =
        p.copy(name = p.name.replace('.', ' ').replace('-', ' '))

    private fun detectTemporada(p: ParsedName): ParsedName {
        val season = TEMPORADA_PATTERN.find(p.name)?.groupValues?.get(1)?.toInt()
        return p.copy(detectedTemporada = season)
    }

    private fun processChapterInfo(p: ParsedName): ParsedName {
        val match = CAP_PATTERN.find(p.name) ?: return p
        val capValue = match.groupValues[1].toInt()
        val baseName = p.name.substring(0, match.range.first)

        return if (p.season != null) {
            p.copy(name = baseName)
        } else {
            val (season, episode) = resolveSeason(capValue, p.detectedTemporada)
            p.copy(name = baseName, season = season, episode = episode)
        }
    }

    private fun resolveSeason(cap: Int, detectedTemporada: Int?): Pair<Int, Int> = when {
        detectedTemporada != null -> detectedTemporada to (cap % 100)
        cap >= 100 -> (cap / 100) to (cap % 100)
        else -> 1 to cap
    }

    private fun removeMetadataKeywords(p: ParsedName): ParsedName = p.copy(
        name = p.name
            .replace(TEMPORADA_CLEAN_PATTERN, "")
            .replace(CAP_CLEAN_PATTERN, "")
    )

    private fun applyNoiseFilter(p: ParsedName): ParsedName {
        val noisePattern = buildNoisePattern()
        return p.copy(name = noisePattern.replace(p.name, " "))
    }

    private fun cleanupRemnants(p: ParsedName): ParsedName = p.copy(
        name = p.name
            .replace(REMNANTS_PATTERN, "")
            .replace(MULTIPLE_SPACES_PATTERN, " ")
            .trim()
    )

    private fun buildNoisePattern(): Regex {
        val terms = noiseTermsProvider().filter { it.isNotBlank() }
        if (terms.isEmpty()) return NO_MATCH_PATTERN

        val alternation = terms.joinToString("|") { Regex.escape(it) }
        return Regex("(?i)\\b($alternation)\\b")
    }

    private fun buildCleanResult(p: ParsedName): CleanResult = CleanResult(
        cleanedName = formatCleanedName(p),
        season = p.season,
        episode = p.episode
    )

    private fun formatCleanedName(p: ParsedName): String = buildString {
        append(capitalizeTitle(p.name))
        p.year?.let { append(" ($it)") }
        if (p.season != null && p.episode != null) {
            append(" - S${p.season.format()}E${p.episode.format()}")
        }
    }

    private fun capitalizeTitle(title: String): String =
        title.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }

    private fun Int.format(): String = toString().padStart(2, '0')

    companion object {
        private val SE_PATTERN = Regex("""(?i)\bS(\d{2})E(\d{2})\b""")
        private val BRACKET_PATTERN = Regex("""\[(?!Cap[. ]?\d+).*?]""")
        private val PARENTHESIS_PATTERN = Regex("""\(([^)]*)\)""")
        private val YEAR_PATTERN = Regex("""19\d{2}|20\d{2}""")
        private val TEMPORADA_PATTERN = Regex("""(?i)\btemporada\s*(\d{1,2})\b""")
        private val CAP_PATTERN = Regex("""(?i)\bcap(?:\.?|itulo|Título)?\s*(\d{2,3})\b""")
        private val TEMPORADA_CLEAN_PATTERN = Regex("""(?i)\btemporada\s*\d{1,2}\b""")
        private val CAP_CLEAN_PATTERN = Regex("""(?i)\bcap(?:\.?|itulo|Título)?\s*\d{1,3}\b""")
        private val REMNANTS_PATTERN = Regex("""[\[\]()]""")
        private val MULTIPLE_SPACES_PATTERN = Regex("""\s{2,}""")
        private val NO_MATCH_PATTERN = Regex("(?!)")
    }
}
