package my.skyme32.mediacleaner.data.settings

import com.russhwolf.settings.Settings

/**
 * Gestión de configuración de la app con Multiplatform Settings.
 * SRP: solo gestiona preferencias del usuario.
 */
class AppSettings(private val settings: Settings) {

    companion object {
        private const val KEY_LAST_FOLDER = "last_folder"
        private const val KEY_RECURSIVE = "recursive_scan"
        private const val KEY_NOISE_TERMS = "noise_terms"
        private const val TERM_SEPARATOR = "\n"

        /** Términos/patrones de ruido por defecto. */
        val DEFAULT_NOISE_TERMS: List<String> = listOf(
            "480p", "720p", "1080p", "2160p", "4k",
            "x264", "x265", "h264", "h265", "hevc", "avc",
            "hdr", "hdr10", "dv", "dolby",
            "web", "webdl", "webrip", "hdtv", "hdvt", "bluray", "bdrip", "dvdrip",
            "aac", "ac3", "ddp", "dts",
            "subs", "subbed",
            "proper", "repack", "extended",
            "eztv", "rarbg", "yify", "ettv",
            "amzn", "nf", "hmax",
            "megusta", "elite", "flux", "ion10", "psa",
            // Patrón regex avanzado: dominios web
            """[a-z]{2,}\.(com|net|org|to)"""
        )
    }

    var lastFolder: String
        get() = settings.getString(KEY_LAST_FOLDER, "")
        set(value) = settings.putString(KEY_LAST_FOLDER, value)

    var recursiveScan: Boolean
        get() = settings.getBoolean(KEY_RECURSIVE, true)
        set(value) = settings.putBoolean(KEY_RECURSIVE, value)

    /**
     * Lista de términos/patrones de ruido almacenados como string separado por \n.
     * Soporta regex (ej: [a-z]{2,}\.(com|net|org|to)) además de palabras simples.
     */
    var noiseTerms: List<String>
        get() {
            val raw = settings.getString(KEY_NOISE_TERMS, "")
            return if (raw.isBlank()) DEFAULT_NOISE_TERMS
            else raw.split(TERM_SEPARATOR).map { it.trim() }.filter { it.isNotBlank() }
        }
        set(value) = settings.putString(KEY_NOISE_TERMS, value.joinToString(TERM_SEPARATOR))

    fun resetNoiseTermsToDefault() {
        noiseTerms = DEFAULT_NOISE_TERMS
    }
}
