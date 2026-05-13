package my.skyme32.mediacleaner.domain.usecase

import my.skyme32.mediacleaner.domain.cleaner.TitleCleaner
import my.skyme32.mediacleaner.domain.model.CleanResult

class CleanTitleUseCase(private val cleaner: TitleCleaner) {
    operator fun invoke(rawName: String): CleanResult = cleaner.clean(rawName)
}

