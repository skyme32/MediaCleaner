package my.skyme32.mediacleaner.data.file

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import my.skyme32.mediacleaner.domain.model.RenameResult
import my.skyme32.mediacleaner.domain.usecase.FolderProcessor
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class OkioFolderProcessor(
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val cleanName: (String) -> String,
    private val recursive: Boolean = true,
    private val allowedExtensions: Set<String> = DEFAULT_MEDIA_EXTENSIONS
) : FolderProcessor {

    companion object {
        val DEFAULT_MEDIA_EXTENSIONS: Set<String> = setOf(
            ".mkv", ".mp4", ".avi", ".mov", ".wmv",
            ".m4v", ".ts", ".m2ts", ".flv", ".webm"
        )
    }

    override suspend fun processFolder(path: String): List<RenameResult> =
        resultsFlow(path).toList()

    fun resultsFlow(path: String): Flow<RenameResult> = flow {
        processPath(path.toPath()) { emit(it) }
    }.flowOn(ioDispatcher)

    private suspend fun processPath(dir: Path, emit: suspend (RenameResult) -> Unit) {
        val entries = runCatching { fileSystem.list(dir) }.getOrElse { return }

        for (entry in entries) {
            val metadata = fileSystem.metadataOrNull(entry) ?: continue
            if (metadata.isDirectory) {
                if (recursive) processPath(entry, emit)
                continue
            }
            processFile(entry, emit)
        }
    }

    private suspend fun processFile(entry: Path, emit: suspend (RenameResult) -> Unit) {
        val originalName = entry.name
        val (nameWithoutExt, extension) = splitExtension(originalName)

        if (allowedExtensions.isNotEmpty() && extension.lowercase() !in allowedExtensions) {
            return
        }

        val cleanedBase = cleanName(nameWithoutExt)
        if (cleanedBase == nameWithoutExt) {
            emit(RenameResult.Skipped(originalName, "Sin cambios"))
            return
        }

        val newName = cleanedBase + extension
        val parent = entry.parent
            ?: run {
                emit(RenameResult.Error(originalName, "No se puede determinar el directorio padre"))
                return
            }
        val newPath = parent / newName

        if (fileSystem.exists(newPath)) {
            emit(RenameResult.Skipped(originalName, "Ya existe: $newName"))
            return
        }

        runCatching { fileSystem.atomicMove(entry, newPath) }
            .onSuccess { emit(RenameResult.Success(originalName, newName)) }
            .onFailure { emit(RenameResult.Error(originalName, it.message ?: "Error desconocido")) }
    }

    private fun splitExtension(name: String): Pair<String, String> {
        val idx = name.lastIndexOf('.')
        return if (idx != -1) name.substring(0, idx) to name.substring(idx)
        else name to ""
    }
}
