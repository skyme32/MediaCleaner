package my.skyme32.mediacleaner.presentation

import my.skyme32.mediacleaner.domain.model.MediaEntry
import my.skyme32.mediacleaner.domain.model.RenameResult

data class MainUiState(
    val folderPath: String = "",
    val recursive: Boolean = true,
    val isProcessing: Boolean = false,
    val results: List<RenameResult> = emptyList(),
    val history: List<MediaEntry> = emptyList(),
    val selectedTab: Tab = Tab.PROCESS,
    val previewResult: String = "",
    val noiseTermsText: String = "",
    val noiseTermsError: String? = null
) {
    enum class Tab { PROCESS, HISTORY, SETTINGS }
}
