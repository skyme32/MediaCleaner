package my.skyme32.mediacleaner.data.setings

import com.russhwolf.settings.Settings
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import my.skyme32.mediacleaner.data.settings.AppSettings
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class AppSettingsTest {

    @MockK
    private lateinit var mockSettings: Settings
    private lateinit var appSettings: AppSettings

    private fun getKey(fieldName: String): String {
        return AppSettings::class.java.getDeclaredField(fieldName).let {
            it.isAccessible = true
            it.get(null) as String
        }
    }

    @Before
    fun setUp() {
        // Inicializa los mocks de MockK
        MockKAnnotations.init(this)
        appSettings = AppSettings(mockSettings)
    }

    @Test
    fun `lastFolder - writes and reads correctly`() {
        val key = getKey("KEY_LAST_FOLDER")

        every { mockSettings.getString(key, "") } returns "OldPath"
        assertEquals("OldPath", appSettings.lastFolder)
        val newPath = "New/Test/Folder"
        every { mockSettings.putString(key, newPath) } returns Unit

        appSettings.lastFolder = newPath

        verify { mockSettings.putString(key, newPath) }
    }

    @Test
    fun `recursiveScan - writes and reads correctly`() {
        val key = getKey("KEY_RECURSIVE")
        every { mockSettings.getBoolean(key, true) } returns false
        every { mockSettings.putBoolean(key, any()) } returns Unit

        assertEquals(false, appSettings.recursiveScan)

        val newValue = true
        appSettings.recursiveScan = newValue

        verify { mockSettings.putBoolean(key, newValue) }
    }

    @Test
    fun `noiseTerms - on initial load with no data returns default list`() {
        val key = getKey("KEY_NOISE_TERMS")
        every { mockSettings.getString(key, "") } returns ""

        val expectedDefaults = AppSettings.DEFAULT_NOISE_TERMS
        assertEquals(expectedDefaults, appSettings.noiseTerms)

        verify { mockSettings.getString(key, "") }
    }

    @Test
    fun `noiseTerms - on initial load with custom data returns parsed list`() {
        val key = getKey("KEY_NOISE_TERMS")
        val customTermsString = "term1\nregex[a-z]{2,}\\.(com|net|org)\n\n\nterm3"
        every { mockSettings.getString(key, "") } returns customTermsString

        val expectedTerms = listOf("term1", "regex[a-z]{2,}\\.(com|net|org)", "term3")
        assertEquals(expectedTerms, appSettings.noiseTerms)
    }

    @Test
    fun `noiseTerms - writing a new set of terms saves correctly`() {
        val key = getKey("KEY_NOISE_TERMS")
        every { mockSettings.putString(key, any()) } returns Unit

        val newTerms = listOf("abc", "xyz", "another_term")
        appSettings.noiseTerms = newTerms

        val expectedString = "abc\nxyz\nanother_term"
        verify { mockSettings.putString(key, expectedString) }
    }

    @Test
    fun `resetNoiseTermsToDefault - resets terms and saves new default list`() {
        val key = getKey("KEY_NOISE_TERMS")
        val sepKey = getKey("TERM_SEPARATOR")
        val separator = AppSettings::class.java.getDeclaredField("TERM_SEPARATOR").let {
            it.isAccessible = true
            it.get(null) as String
        }

        every { mockSettings.putString(key, any()) } returns Unit

        appSettings.resetNoiseTermsToDefault()

        val expectedString = AppSettings.DEFAULT_NOISE_TERMS.joinToString(separator)
        verify { mockSettings.putString(key, expectedString) }
    }
}