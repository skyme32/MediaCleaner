package my.skyme32.mediacleaner.data.file

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import my.skyme32.mediacleaner.domain.model.RenameResult
import okio.FileMetadata
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class OkioFolderProcessorTest {

    private val mockFileSystem = mockk<FileSystem>(relaxed = true)
    private val fakeCleanName: (String) -> String = { it.replace("bad", "good") }
    private lateinit var processor: OkioFolderProcessor

    @BeforeTest
    fun setup() {
        processor = OkioFolderProcessor(
            fileSystem = mockFileSystem,
            cleanName = fakeCleanName,
            recursive = true
        )
    }

    @Test
    fun `processFile renames file when name is dirty`() = runTest {
        val folder = "test".toPath()
        val file = "test/bad_name.mp4".toPath()
        val expectedNewPath = "test/good_name.mp4".toPath()

        every { mockFileSystem.list(folder) } returns listOf(file)

        every { mockFileSystem.metadataOrNull(file) } returns FileMetadata(
            isRegularFile = true,
            isDirectory = false,
            size = 100L
        )

        every { mockFileSystem.exists(expectedNewPath) } returns false

        val results = processor.processFolder("test")

        assertTrue(results.any { it is RenameResult.Success }, "Debería ser un éxito")
        verify { mockFileSystem.atomicMove(file, expectedNewPath) }
    }

    @Test
    fun `processFile skips if name is already clean`() = runTest {
        val folder = "test".toPath()
        val cleanFile = "test/good_name.mp4".toPath()

        every { mockFileSystem.list(folder) } returns listOf(cleanFile)
        every { mockFileSystem.metadataOrNull(cleanFile) } returns FileMetadata(isRegularFile = true)

        val results = processor.processFolder("test")

        assertTrue(results.any { it is RenameResult.Skipped }, "Debería saltarse")
        verify(exactly = 0) { mockFileSystem.atomicMove(any(), any()) }
    }
}