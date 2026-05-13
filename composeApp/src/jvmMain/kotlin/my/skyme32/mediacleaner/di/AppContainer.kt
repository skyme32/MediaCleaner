package my.skyme32.mediacleaner.di

import com.russhwolf.settings.PreferencesSettings
import my.skyme32.mediacleaner.data.cleaner.TitleCleanerImpl
import my.skyme32.mediacleaner.data.db.DatabaseFactory
import my.skyme32.mediacleaner.data.db.HistoryRepositoryImpl
import my.skyme32.mediacleaner.data.file.OkioFolderProcessor
import my.skyme32.mediacleaner.data.settings.AppSettings
import my.skyme32.mediacleaner.domain.usecase.CleanTitleUseCase
import my.skyme32.mediacleaner.domain.usecase.ClearHistoryUseCase
import my.skyme32.mediacleaner.domain.usecase.GetHistoryUseCase
import my.skyme32.mediacleaner.domain.usecase.ProcessFolderUseCase
import my.skyme32.mediacleaner.presentation.MainViewModel
import java.util.prefs.Preferences

/**
 * Contenedor de dependencias simple (DIP: las capas dependen de abstracciones).
 * Se podría reemplazar por Koin/Kodein si el proyecto crece.
 */
object AppContainer {

    private val appDir: String = System.getProperty("user.home") + "/.mediacleaner"

    // Settings
    private val preferences = Preferences.userRoot().node("my.skyme32.mediacleaner")
    val appSettings = AppSettings(PreferencesSettings(preferences))

    // Cleaner — noiseTerms se leen de settings en cada limpieza (cambios en caliente)
    private val titleCleaner = TitleCleanerImpl(noiseTermsProvider = { appSettings.noiseTerms })
    val cleanTitleUseCase = CleanTitleUseCase(titleCleaner)

    // Database
    private val database = DatabaseFactory.create("$appDir/history.db")
    private val historyRepository = HistoryRepositoryImpl(database)

    // File processor
    private fun buildFolderProcessor(): OkioFolderProcessor =
        OkioFolderProcessor(
            cleanName = { rawName -> titleCleaner.clean(rawName).cleanedName },
            recursive = appSettings.recursiveScan,
            allowedExtensions = OkioFolderProcessor.DEFAULT_MEDIA_EXTENSIONS
        )

    // Use cases
    val getHistoryUseCase = GetHistoryUseCase(historyRepository)
    val clearHistoryUseCase = ClearHistoryUseCase(historyRepository)

    fun buildProcessFolderUseCase(): ProcessFolderUseCase =
        ProcessFolderUseCase(
            folderProcessor = buildFolderProcessor(),
            cleanTitle = cleanTitleUseCase,
            repository = historyRepository
        )

    // ViewModel factory
    fun provideViewModel(): MainViewModel = MainViewModel(
        appSettings = appSettings,
        cleanTitleUseCase = cleanTitleUseCase,
        processFolderUseCaseFactory = ::buildProcessFolderUseCase,
        getHistoryUseCase = getHistoryUseCase,
        clearHistoryUseCase = clearHistoryUseCase
    )
}

