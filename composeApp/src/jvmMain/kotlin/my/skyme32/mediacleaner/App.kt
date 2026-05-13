package my.skyme32.mediacleaner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.skyme32.mediacleaner.di.AppContainer
import my.skyme32.mediacleaner.presentation.MainUiState
import my.skyme32.mediacleaner.presentation.MainViewModel
import my.skyme32.mediacleaner.presentation.screens.HistoryScreen
import my.skyme32.mediacleaner.presentation.screens.ProcessScreen
import my.skyme32.mediacleaner.presentation.screens.SettingsScreen

@Composable
fun App(viewModel: MainViewModel = remember { AppContainer.provideViewModel() }) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab bar
            SecondaryTabRow(selectedTabIndex = state.selectedTab.ordinal) {
                MainUiState.Tab.entries.forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.onTabSelected(tab) },
                        text = {
                            Text(when (tab) {
                                MainUiState.Tab.PROCESS -> "Procesar"
                                MainUiState.Tab.HISTORY -> "Historial"
                                MainUiState.Tab.SETTINGS -> "Ajustes"
                            })
                        }
                    )
                }
            }

            // Screens
            when (state.selectedTab) {
                MainUiState.Tab.PROCESS -> ProcessScreen(state, viewModel)
                MainUiState.Tab.HISTORY -> HistoryScreen(state, viewModel)
                MainUiState.Tab.SETTINGS -> SettingsScreen(state, viewModel)
            }
        }
    }
}
