package my.skyme32.mediacleaner.domain.usecase

import my.skyme32.mediacleaner.domain.model.MediaEntry
import my.skyme32.mediacleaner.domain.repository.HistoryRepository

class GetHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(): List<MediaEntry> = repository.getAll()
}

class ClearHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke() = repository.clear()
}

