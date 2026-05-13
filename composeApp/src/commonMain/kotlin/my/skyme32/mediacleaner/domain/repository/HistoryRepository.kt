package my.skyme32.mediacleaner.domain.repository

import my.skyme32.mediacleaner.domain.model.MediaEntry

interface HistoryRepository {
    suspend fun save(entry: MediaEntry)
    suspend fun getAll(): List<MediaEntry>
    suspend fun clear()
}

