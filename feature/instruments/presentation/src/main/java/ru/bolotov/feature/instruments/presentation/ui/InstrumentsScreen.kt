package ru.bolotov.feature.instruments.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.orbitmvi.orbit.compose.collectAsState
import ru.bolotov.feature.instruments.presentation.core.Action
import ru.bolotov.feature.instruments.presentation.core.InstrumentFiltersUi
import ru.bolotov.feature.instruments.presentation.core.InstrumentUi
import ru.bolotov.feature.instruments.presentation.core.InstrumentsViewModel
import ru.bolotov.feature.instruments.presentation.core.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InstrumentsScreen(viewModel: InstrumentsViewModel) {
    val state by viewModel.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Инструменты бота") }) },
        bottomBar = {
            Button(
                onClick = { viewModel.handleAction(Action.Rescan) },
                enabled = !state.isRescanning,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                if (state.isRescanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp).padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(if (state.isRescanning) "Выполняется рескан…" else "Рескан")
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.handleAction(Action.Load) },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                state.isLoading && state.instruments.isEmpty() -> ScreenMessage(paddingValues) {
                    CircularProgressIndicator()
                }
                state.errorMessage != null -> ScreenMessage(paddingValues) {
                    Text(state.errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error)
                }
                else -> InstrumentsList(state, paddingValues) {
                    viewModel.handleAction(Action.ShowFilters)
                }
            }
        }
    }

    state.filters?.takeIf { state.showFiltersDialog }?.let { filters ->
        FiltersDialog(
            filters = filters,
            onSave = { viewModel.handleAction(Action.SaveFilters(it)) },
            onDismiss = { viewModel.handleAction(Action.DismissFilters) }
        )
    }
}

@Composable
private fun ScreenMessage(paddingValues: PaddingValues, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) { content() }
}

@Composable
private fun InstrumentsList(
    state: UiState,
    paddingValues: PaddingValues,
    onShowFilters: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        state.filters?.let { filters ->
            item { FiltersCard(filters, onShowFilters) }
        }
        item {
            InstrumentsCount(state.instruments.size)
        }

        if (state.instruments.isEmpty()) {
            item { EmptyInstrumentsMessage() }
        } else {
            items(state.instruments, key = { it.id }) { instrument ->
                InstrumentCard(instrument)
            }
        }
    }
}

@Composable
private fun InstrumentsCount(count: Int) {
    Text(
        text = "Бот отслеживает $count инструментов",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp
    )
}

@Composable
private fun EmptyInstrumentsMessage() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("Нет выбранных инструментов")
        Text(
            text = "Измените фильтры или запустите рескан, чтобы сформировать список",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FiltersCard(filters: InstrumentFiltersUi, onShowFilters: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Фильтры отбора", fontWeight = FontWeight.SemiBold)
            Text(
                "Объём от ${filters.minDailyVolume}, " +
                    "волатильность ${filters.minVolatility}–${filters.maxVolatility}%, " +
                    "максимум ${filters.maxCount}"
            )
            Button(onClick = onShowFilters) { Text("Изменить фильтры") }
        }
    }
}

@Composable
private fun FiltersDialog(
    filters: InstrumentFiltersUi,
    onSave: (InstrumentFiltersUi) -> Unit,
    onDismiss: () -> Unit
) {
    var volume by remember { mutableStateOf(filters.minDailyVolume.toString()) }
    var minVolatility by remember { mutableStateOf(filters.minVolatility.toString()) }
    var maxVolatility by remember { mutableStateOf(filters.maxVolatility.toString()) }
    var maxCount by remember { mutableStateOf(filters.maxCount.toString()) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Фильтры отбора") },
        text = {
            Column {
                androidx.compose.material3.OutlinedTextField(
                    value = volume,
                    onValueChange = { volume = it },
                    label = { Text("Мин. дневной объём") }
                )
                androidx.compose.material3.OutlinedTextField(
                    value = minVolatility,
                    onValueChange = { minVolatility = it },
                    label = { Text("Мин. волатильность, %") }
                )
                androidx.compose.material3.OutlinedTextField(
                    value = maxVolatility,
                    onValueChange = { maxVolatility = it },
                    label = { Text("Макс. волатильность, %") }
                )
                androidx.compose.material3.OutlinedTextField(
                    value = maxCount,
                    onValueChange = { maxCount = it },
                    label = { Text("Макс. инструментов") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        InstrumentFiltersUi(
                            minDailyVolume = volume.toLongOrNull() ?: filters.minDailyVolume,
                            minVolatility = minVolatility.toDoubleOrNull() ?: filters.minVolatility,
                            maxVolatility = maxVolatility.toDoubleOrNull() ?: filters.maxVolatility,
                            maxCount = maxCount.toIntOrNull() ?: filters.maxCount
                        )
                    )
                }
            ) { Text("Сохранить") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun InstrumentCard(instrument: InstrumentUi) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = instrument.ticker, fontWeight = FontWeight.SemiBold)
            Text(
                text = instrument.name,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
