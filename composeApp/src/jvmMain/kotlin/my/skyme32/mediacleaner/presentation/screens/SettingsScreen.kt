package my.skyme32.mediacleaner.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import my.skyme32.mediacleaner.presentation.MainUiState
import my.skyme32.mediacleaner.presentation.MainViewModel

@Composable
fun SettingsScreen(state: MainUiState, viewModel: MainViewModel) {
    val termCount = state.noiseTermsText.split("\n").count { it.isNotBlank() }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ajustes", style = MaterialTheme.typography.titleLarge)

        HorizontalDivider()

        NoiseTermsHeader(
            termCount = termCount,
            saveEnabled = state.noiseTermsError == null,
            onSave = viewModel::saveNoiseTerms,
            onReset = viewModel::resetNoiseTermsToDefault
        )

        Text(
            "Escribe un término o patrón regex por línea. " +
            "Se eliminan del nombre del fichero al limpiar. " +
            "Los cambios se aplican en caliente sin reiniciar.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (state.noiseTermsError != null) {
            NoiseTermsErrorBanner(message = state.noiseTermsError)
        }

        NoiseTermsEditor(
            text = state.noiseTermsText,
            isError = state.noiseTermsError != null,
            onTextChanged = viewModel::onNoiseTermsTextChanged,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}

@Composable
private fun NoiseTermsHeader(
    termCount: Int,
    saveEnabled: Boolean,
    onSave: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Términos de ruido ($termCount)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        OutlinedButton(onClick = onReset) {
            Text("Restablecer")
        }
        Button(onClick = onSave, enabled = saveEnabled) {
            Text("Guardar")
        }
    }
}

@Composable
private fun NoiseTermsErrorBanner(message: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("⚠", color = MaterialTheme.colorScheme.onErrorContainer)
            Text(
                message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun NoiseTermsEditor(
    text: String,
    isError: Boolean,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        modifier = modifier,
        label = { Text("Un término o regex por línea") },
        isError = isError,
        placeholder = { Text("480p\n720p\n1080p\n...") }
    )
}

