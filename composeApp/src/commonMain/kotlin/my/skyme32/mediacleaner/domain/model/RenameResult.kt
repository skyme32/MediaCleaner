package my.skyme32.mediacleaner.domain.model

sealed class RenameResult {
    data class Success(
        val originalName: String,
        val newName: String
    ) : RenameResult()

    data class Skipped(
        val name: String,
        val reason: String
    ) : RenameResult()

    data class Error(
        val name: String,
        val message: String
    ) : RenameResult()
}

