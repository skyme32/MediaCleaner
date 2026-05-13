package my.skyme32.mediacleaner.data.cleaner

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TitleCleanerImplTest {

    private val noiseTerms = listOf("1080p", "BluRay", "x264", "Dual", "AC3", "mkv", "WEB-DL")
    private val cleaner = TitleCleanerImpl(noiseTermsProvider = { noiseTerms })

    @Test
    fun `clean movie with year and noise`() {
        val input = "the.matrix.1999.1080p.BluRay.x264.mkv"
        val result = cleaner.clean(input)

        assertEquals("The matrix (1999)", result.cleanedName)
        assertNull(result.season)
        assertNull(result.episode)
    }

    @Test
    fun `clean series with S01E01 format`() {
        val input = "better.call.saul.S01E02.720p.WEB-DL"
        val result = cleaner.clean(input)

        assertEquals("Better call saul - S01E02", result.cleanedName)
        assertEquals(1, result.season)
        assertEquals(2, result.episode)
    }

    @Test
    fun `capitalize only the first character of the title`() {
        val input = "the matrix reloaded"
        val result = cleaner.clean(input)
        
        assertEquals("The matrix reloaded", result.cleanedName)
    }

    @Test
    fun `preserve mixed casing like iPad when first letter is lowercase`() {
        val input = "the iPad documentary"
        val result = cleaner.clean(input)
        
        assertEquals("The iPad documentary", result.cleanedName)
    }

    @Test
    fun `capitalize after removing leading separators and noise`() {
        val input = "...[1080p]...the matrix..."
        val result = cleaner.clean(input)
        
        assertEquals("The matrix", result.cleanedName)
    }

    @Test
    fun `handle titles starting with numbers`() {
        val input = "300 movie"
        val result = cleaner.clean(input)
        
        assertEquals("300 movie", result.cleanedName)
    }

    @Test
    fun `clean series with Temporada and Cap format`() {
        val input = "los Simpsons Temporada 10 Cap 15 Dual"
        val result = cleaner.clean(input)

        assertEquals("Los Simpsons - S10E15", result.cleanedName)
        assertEquals(10, result.season)
        assertEquals(15, result.episode)
    }

    @Test
    fun `clean series with 3-digit Cap resolving to season and episode`() {
        val input = "one piece Cap 105"
        val result = cleaner.clean(input)

        assertEquals("One piece - S01E05", result.cleanedName)
        assertEquals(1, result.season)
        assertEquals(5, result.episode)
    }

    @Test
    fun `remove brackets and parentheses but keep year`() {
        val input = "movie name [4K] (2022) (Internal)"
        val result = cleaner.clean(input)

        assertEquals("Movie name (2022)", result.cleanedName)
    }

    @Test
    fun `normalize separators like dots and dashes`() {
        val input = "the-movie.title.2020.Dual"
        val result = cleaner.clean(input)

        assertEquals("The movie title (2020)", result.cleanedName)
    }

    @Test
    fun `extract year even if inside parentheses`() {
        val input = "matrix (1999) 1080p"
        val result = cleaner.clean(input)

        assertEquals("Matrix (1999)", result.cleanedName)
    }

    @Test
    fun `clean title with multiple spaces and noise`() {
        val input = "  some   movie   title   Dual  "
        val result = cleaner.clean(input)

        assertEquals("Some movie title", result.cleanedName)
    }
}
