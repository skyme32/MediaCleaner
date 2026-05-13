package my.skyme32.mediacleaner.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import my.skyme32.mediacleaner.domain.model.MediaEntry
import my.skyme32.mediacleaner.presentation.MainUiState
import my.skyme32.mediacleaner.presentation.MainViewModel

@Composable
fun HistoryScreen(state: MainUiState, viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HistoryHeader(
            count = state.history.size,
            onClear = viewModel::clearHistory,
            clearEnabled = state.history.isNotEmpty()
        )

        if (state.history.isEmpty()) {
            EmptyHistoryPlaceholder()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.history) { entry ->
                    HistoryEntryItem(entry)
                }
            }
        }
    }
}

@Composable
private fun HistoryHeader(count: Int, onClear: () -> Unit, clearEnabled: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Historial ($count entradas)",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedButton(onClick = onClear, enabled = clearEnabled) {
            Text("Limpiar historial")
        }
    }
}

@Composable
private fun EmptyHistoryPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sin historial todavía.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HistoryEntryItem(entry: MediaEntry) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Badge { Text(entry.mediaType.name) }
                if (entry.season != null) {
                    Badge { Text("S%02dE%02d".format(entry.season, entry.episode)) }
                }
            }
            Text(entry.cleanedName, style = MaterialTheme.typography.bodyMedium)
            Text(
                "← ${entry.originalName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
