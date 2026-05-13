package my.skyme32.mediacleaner.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import my.skyme32.mediacleaner.domain.model.RenameResult
import my.skyme32.mediacleaner.presentation.MainUiState
import my.skyme32.mediacleaner.presentation.MainViewModel
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun ProcessScreen(state: MainUiState, viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FolderPickerRow(
            folderPath = state.folderPath,
            onPathChanged = viewModel::onFolderPathChanged
        )

        RecursiveOption(
            checked = state.recursive,
            onCheckedChange = viewModel::onRecursiveChanged
        )

        PreviewRow(
            previewResult = state.previewResult,
            onInputChanged = viewModel::onPreviewName
        )

        ProcessButton(
            isProcessing = state.isProcessing,
            enabled = !state.isProcessing && state.folderPath.isNotBlank(),
            onClick = viewModel::processFolder
        )

        if (state.results.isNotEmpty()) {
            ResultsSummary(results = state.results)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(state.results) { result ->
                RenameResultItem(result)
            }
        }
    }
}

@Composable
private fun FolderPickerRow(folderPath: String, onPathChanged: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = folderPath,
            onValueChange = onPathChanged,
            label = { Text("Carpeta de medios") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Button(onClick = {
            val dialog = FileDialog(null as Frame?, "Seleccionar carpeta", FileDialog.LOAD)
            dialog.isVisible = true
            dialog.directory?.let(onPathChanged)
        }) {
            Text("Examinar")
        }
    }
}

@Composable
private fun RecursiveOption(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text("Escaneo recursivo")
    }
}

@Composable
private fun PreviewRow(previewResult: String, onInputChanged: (String) -> Unit) {
    var input by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it; onInputChanged(it) },
            label = { Text("Vista previa de limpieza") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (previewResult.isNotBlank()) {
            Text(
                "→ $previewResult",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProcessButton(isProcessing: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isProcessing) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
        }
        Text(if (isProcessing) "Procesando..." else "Iniciar renombrado")
    }
}

@Composable
private fun ResultsSummary(results: List<RenameResult>) {
    val successes = results.filterIsInstance<RenameResult.Success>().size
    val errors = results.filterIsInstance<RenameResult.Error>().size
    val skipped = results.filterIsInstance<RenameResult.Skipped>().size
    Text(
        "✅ $successes renombrados  ⏭ $skipped saltados  ❌ $errors errores",
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun RenameResultItem(result: RenameResult) {
    val (icon, text, color) = when (result) {
        is RenameResult.Success ->
            Triple("✅", "${result.originalName}  →  ${result.newName}", Color(0xFF1B5E20))
        is RenameResult.Skipped ->
            Triple("⏭", "${result.name} (${result.reason})", Color(0xFF5D4037))
        is RenameResult.Error ->
            Triple("❌", "${result.name}: ${result.message}", Color(0xFFB71C1C))
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(icon)
            Text(text, color = color, style = MaterialTheme.typography.bodySmall)
        }
    }
}

