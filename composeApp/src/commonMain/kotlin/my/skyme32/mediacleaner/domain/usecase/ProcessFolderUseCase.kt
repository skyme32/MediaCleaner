package my.skyme32.mediacleaner.domain.usecase

import my.skyme32.mediacleaner.domain.model.MediaEntry
import my.skyme32.mediacleaner.domain.model.RenameResult
import my.skyme32.mediacleaner.domain.repository.HistoryRepository

interface FolderProcessor {
    suspend fun processFolder(path: String): List<RenameResult>
}

class ProcessFolderUseCase(
    private val folderProcessor: FolderProcessor,
    private val cleanTitle: CleanTitleUseCase,
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(path: String): List<RenameResult> {
        val results = folderProcessor.processFolder(path)
        val now = System.currentTimeMillis()

        results.filterIsInstance<RenameResult.Success>().forEach { r ->
            val clean = cleanTitle(r.originalName.substringBeforeLast('.'))
            repository.save(
                MediaEntry(
                    id = 0,
                    originalName = r.originalName,
                    cleanedName = r.newName,
                    mediaType = clean.mediaType,
                    season = clean.season,
                    episode = clean.episode,
                    timestamp = now
                )
            )
        }
        return results
    }
}
