package ru.bolotov.feature.positions.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.feature.positions.presentation.core.Action
import ru.bolotov.feature.positions.presentation.core.Effect
import ru.bolotov.feature.positions.presentation.core.PositionFilter
import ru.bolotov.feature.positions.presentation.core.PositionsViewModel
import ru.bolotov.tradebot.feature.positions.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PositionsScreen(
    viewModel: PositionsViewModel,
    onNavigateToInstrument: (String) -> Unit = {}
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.ShowToast -> {
                // TODO: Show toast implementation
            }

            is Effect.ShowError -> {
                // TODO: Show error dialog
            }

            is Effect.Refresh -> {
                // Handle refresh complete
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Позиции") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.handleAction(Action.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Stats row
                StatsRow(
                    totalPnl = state.totalPnl,
                    openCount = state.openPositionsCount,
                    closedCount = state.closedPositionsCount,
                    modifier = Modifier.padding(16.dp)
                )

                // Filter bar
                PositionsFilterBar(
                    currentFilter = state.filterType,
                    onFilterSelected = { viewModel.handleAction(Action.Filter(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Positions list
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        state.errorMessage != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = state.errorMessage!!,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.handleAction(Action.LoadData) }
                                    ) {
                                        Text("Повторить")
                                    }
                                }
                            }
                        }

                        state.filteredPositions.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Нет позиций")
                                    Text(
                                        text = when (state.filterType) {
                                            PositionFilter.ALL -> "У вас пока нет ни одной позиции"
                                            PositionFilter.OPEN -> "Нет открытых позиций"
                                            PositionFilter.PROFIT -> "Нет прибыльных позиций"
                                            PositionFilter.LOSS -> "Нет убыточных позиций"
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.filteredPositions) { position ->
                                    PositionCard(
                                        position = position,
                                        onCloseClick = {
                                            viewModel.handleAction(Action.ClosePosition(position.id))
                                        },
                                        onInstrumentClick = {
                                            viewModel.handleAction(
                                                Action.NavigateToInstrument(
                                                    position.instrumentId
                                                )
                                            )
                                            onNavigateToInstrument(position.instrumentId)
                                        },
                                        onInfoClick = {
                                            viewModel.handleAction(Action.ShowPositionInfo(position.id))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Close confirmation dialog
    state.closingPosition?.let { position ->
        ClosePositionDialog(
            positionName = position.instrumentName,
            onConfirm = {
                viewModel.handleAction(Action.ConfirmClose)
            },
            onDismiss = {
                viewModel.handleAction(Action.DismissCloseDialog)
            }
        )
    }

    state.infoPosition?.let { position ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.handleAction(Action.DismissPositionInfo) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(
                        if (position.isClosed) {
                            R.string.position_close_reason_title
                        } else {
                            R.string.position_ai_explanation_title
                        }
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(12.dp))
                Text(position.infoExplanation.orEmpty())
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatsRow(
    totalPnl: Double,
    openCount: Int,
    closedCount: Int,
    modifier: Modifier = Modifier
) {
    val isPositive = totalPnl >= 0

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                title = "Общий P&L",
                value = "${if (totalPnl > 0) "+" else ""}${String.format("%.2f", totalPnl)} ₽",
                valueColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
            )

            StatItem(
                title = "Открыто",
                value = "$openCount",
                valueColor = MaterialTheme.colorScheme.primary
            )

            StatItem(
                title = "Закрыто",
                value = "$closedCount",
                valueColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = valueColor
        )
    }
}
