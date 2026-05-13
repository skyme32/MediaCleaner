package my.skyme32.mediacleaner.domain.cleaner

import my.skyme32.mediacleaner.domain.model.CleanResult

interface TitleCleaner {
    fun clean(rawName: String): CleanResult
}

