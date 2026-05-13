package my.skyme32.mediacleaner.data.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.skyme32.mediacleaner.db.MediaCleanerDatabase
import my.skyme32.mediacleaner.domain.model.MediaEntry
import my.skyme32.mediacleaner.domain.model.MediaType
import my.skyme32.mediacleaner.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val database: MediaCleanerDatabase
) : HistoryRepository {

    private val queries get() = database.mediaHistoryQueries

    override suspend fun save(entry: MediaEntry) = withContext(Dispatchers.IO) {
        queries.insertEntry(
            originalName = entry.originalName,
            cleanedName = entry.cleanedName,
            mediaType = entry.mediaType.name,
            season = entry.season?.toLong(),
            episode = entry.episode?.toLong(),
            timestamp = entry.timestamp
        )
    }

    override suspend fun getAll(): List<MediaEntry> = withContext(Dispatchers.IO) {
        queries.selectAll().executeAsList().map { row ->
            MediaEntry(
                id = row.id,
                originalName = row.originalName,
                cleanedName = row.cleanedName,
                mediaType = MediaType.valueOf(row.mediaType),
                season = row.season?.toInt(),
                episode = row.episode?.toInt(),
                timestamp = row.timestamp
            )
        }
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        queries.deleteAll()
    }
}

