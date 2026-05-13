package my.skyme32.mediacleaner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import my.skyme32.mediacleaner.data.settings.AppSettings
import my.skyme32.mediacleaner.domain.usecase.CleanTitleUseCase
import my.skyme32.mediacleaner.domain.usecase.ClearHistoryUseCase
import my.skyme32.mediacleaner.domain.usecase.GetHistoryUseCase
import my.skyme32.mediacleaner.domain.usecase.ProcessFolderUseCase

class MainViewModel(
    private val appSettings: AppSettings,
    private val cleanTitleUseCase: CleanTitleUseCase,
    private val processFolderUseCaseFactory: () -> ProcessFolderUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(
            folderPath = appSettings.lastFolder,
            recursive = appSettings.recursiveScan,
            noiseTermsText = appSettings.noiseTerms.joinToString("\n")
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onFolderPathChanged(path: String) {
        appSettings.lastFolder = path
        _uiState.update { it.copy(folderPath = path) }
    }

    fun onRecursiveChanged(recursive: Boolean) {
        appSettings.recursiveScan = recursive
        _uiState.update { it.copy(recursive = recursive) }
    }

    fun onPreviewName(rawName: String) {
        val result = cleanTitleUseCase(rawName)
        _uiState.update { it.copy(previewResult = result.cleanedName) }
    }

    fun onTabSelected(tab: MainUiState.Tab) {
        _uiState.update { it.copy(selectedTab = tab) }
        when (tab) {
            MainUiState.Tab.HISTORY -> loadHistory()
            MainUiState.Tab.SETTINGS -> loadNoiseTerms()
            else -> Unit
        }
    }

    fun processFolder() {
        val path = _uiState.value.folderPath
        if (path.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, results = emptyList()) }
            val useCase = processFolderUseCaseFactory()
            val results = useCase(path)
            _uiState.update { it.copy(isProcessing = false, results = results) }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            val history = getHistoryUseCase()
            _uiState.update { it.copy(history = history) }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearHistoryUseCase()
            _uiState.update { it.copy(history = emptyList()) }
        }
    }

    private fun loadNoiseTerms() {
        _uiState.update { it.copy(noiseTermsText = appSettings.noiseTerms.joinToString("\n")) }
    }

    fun onNoiseTermsTextChanged(text: String) {
        val error = validateNoiseTerms(text)
        _uiState.update { it.copy(noiseTermsText = text, noiseTermsError = error) }
    }

    fun saveNoiseTerms() {
        val text = _uiState.value.noiseTermsText
        val error = validateNoiseTerms(text)
        if (error != null) {
            _uiState.update { it.copy(noiseTermsError = error) }
            return
        }
        val terms = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        appSettings.noiseTerms = terms
        _uiState.update { it.copy(noiseTermsError = null) }
    }

    fun resetNoiseTermsToDefault() {
        appSettings.resetNoiseTermsToDefault()
        _uiState.update {
            it.copy(
                noiseTermsText = AppSettings.DEFAULT_NOISE_TERMS.joinToString("\n"),
                noiseTermsError = null
            )
        }
    }

    private fun validateNoiseTerms(text: String): String? {
        val terms = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        if (terms.isEmpty()) return "La lista no puede estar vacía"
        return try {
            Regex("""(?i)\b(${terms.joinToString("|")})\b""")
            null
        } catch (e: Exception) {
            "Regex inválido: ${e.message}"
        }
    }
}
