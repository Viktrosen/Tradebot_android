package ru.bolotov.feature.dashboard.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.CurrentStrategy
import ru.bolotov.feature.dashboard.domain.model.MarketRegime
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.RiskConfig
import ru.bolotov.feature.dashboard.domain.model.RiskConfigData
import ru.bolotov.feature.dashboard.domain.model.StrategySettings
import ru.bolotov.feature.dashboard.presentation.core.Action
import ru.bolotov.feature.dashboard.presentation.core.DashboardViewModel
import ru.bolotov.feature.dashboard.presentation.core.Effect
import ru.bolotov.feature.dashboard.presentation.core.UiState
import ru.bolotov.tradebot.feature.dashboard.presentation.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToRisk: () -> Unit,
    onNavigateToPositions: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToInstruments: () -> Unit = {}
) {
    val state by viewModel.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.handleAction(Action.ScreenResumed)
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.NavigateToRisk -> onNavigateToRisk()
            is Effect.NavigateToPositions -> onNavigateToPositions()
            is Effect.NavigateToStrategy -> onNavigateToStrategy()
            is Effect.ShowToast -> { /* TODO: Show toast */
            }

            is Effect.ShowError -> { /* TODO: Show error dialog */
            }
        }
    }

    Scaffold(
        topBar = {
            DashboardTopBar(
                isRunning = state.botStatus?.running ?: false,
                allTradingUnavailable = state.botStatus?.allTradingUnavailable ?: false
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
            when {
                state.errorMessage != null -> ErrorScreen(state.errorMessage!!) {
                    viewModel.handleAction(Action.Refresh)
                }

                else -> DashboardContent(
                    state = state,
                    onStartBot = { viewModel.handleAction(Action.StartBot) },
                    onStopBot = { viewModel.handleAction(Action.StopBot) },
                    onNavigateToStrategy = { viewModel.handleAction(Action.NavigateToStrategy) },
                    onNavigateToRisk = { viewModel.handleAction(Action.NavigateToRisk) },
                    onNavigateToPositions = { viewModel.handleAction(Action.NavigateToPositions) },
                    onNavigateToInstruments = onNavigateToInstruments,
                    onShowAllPositions = { viewModel.handleAction(Action.NavigateToPositions) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(isRunning: Boolean, allTradingUnavailable: Boolean) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("TradeBot", fontWeight = FontWeight.Bold)
                BotStatusChip(isRunning)
                TradingAvailabilityChip(allTradingUnavailable)
            }
        },
        actions = {
            IconButton(onClick = { /* Open settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
private fun BotStatusChip(isRunning: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (isRunning) Color(0xFF4CAF50) else Color(0xFFF44336),
        modifier = Modifier
            .clip(CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (isRunning) "RUNNING" else "STOPPED",
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun DashboardContent(
    state: UiState,
    onStartBot: () -> Unit,
    onStopBot: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToRisk: () -> Unit,
    onNavigateToPositions: () -> Unit,
    onNavigateToInstruments: () -> Unit = {},
    onShowAllPositions: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PnLCards(
                totalPnl = state.totalPnl,
                dailyPnl = state.dailyPnl,
                winRate = state.winRate
            )
        }

        item {
            AvailableCashCard(state.availableCash)
        }

        // НОВОЕ: предупреждение при экстремальной волатильности
        if (state.marketRegime?.isWarning() == true) {
            item {
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Торговля приостановлена: экстремальная волатильность рынка",
                            fontSize = 14.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // НОВОЕ: отображение текущего режима рынка
        if (state.marketRegime != null) {
            item {
                MarketRegimeChip(state.marketRegime)
            }
        }

        item {
            BotControlCard(
                isRunning = state.botStatus?.running ?: false,
                onStartBot = onStartBot,
                onStopBot = onStopBot,
                onNavigateToStrategy = onNavigateToStrategy
            )
        }

        item {
            RiskInfoCard(
                riskConfig = state.riskConfig,
                onNavigateToRisk = onNavigateToRisk
            )
        }

        item {
            InstrumentsInfoCard(
                state.botStatus?.activeInstruments?.size ?: 0,
                onNavigateToInstruments
            )
        }

        if (state.positions.isNotEmpty()) {
            item {
                Text(
                    text = "Открытые позиции",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(state.positions.take(5)) { position ->
                PositionItem(position = position)
            }

            if (state.positions.size > 5) {
                item {
                    TextButton(
                        onClick = onShowAllPositions,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Показать все (${state.positions.size}) →")
                    }
                }
            }
        }
    }
}

@Composable
private fun PnLCards(totalPnl: Double, dailyPnl: Double, winRate: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PnLCard(title = "Общий P&L", value = totalPnl, modifier = Modifier.weight(1f))
        PnLCard(title = "Сегодня", value = dailyPnl, modifier = Modifier.weight(1f))
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Win Rate",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${String.format("%.1f", winRate)}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (winRate >= 50) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun AvailableCashCard(availableCash: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Свободные средства",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "${String.format("%,.2f", availableCash)} ₽",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun TradingAvailabilityChip(allTradingUnavailable: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (allTradingUnavailable) Color(0xFFF9A825) else Color(0xFF2E7D32),
        modifier = Modifier
            .clip(CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = stringResource(
                if (allTradingUnavailable) {
                    R.string.dashboard_trading_unavailable_chip
                } else {
                    R.string.dashboard_trading_available_chip
                }
            ),
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun PnLCard(title: String, value: Double, modifier: Modifier = Modifier) {
    val isPositive = value >= 0
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "${if (value > 0) "+" else ""}${String.format("%.2f", value)} ₽",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}

@Composable
private fun BotControlCard(
    isRunning: Boolean,
    onStartBot: () -> Unit,
    onStopBot: () -> Unit,
    onNavigateToStrategy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Управление ботом", fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onNavigateToStrategy) {
                    Text(stringResource(R.string.dashboard_configure_strategies))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onStartBot,
                    enabled = !isRunning,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Старт")
                }

                Button(
                    onClick = onStopBot,
                    enabled = isRunning,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Стоп")
                }
            }
        }
    }
}

@Composable
private fun RiskInfoCard(riskConfig: RiskConfig?, onNavigateToRisk: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToRisk() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.risk_settings_title),
                    fontWeight = FontWeight.SemiBold
                )
                if (riskConfig != null) {
                    Text(
                        text = stringResource(
                            R.string.risk_position_size,
                            riskConfig.config.positionSizePercent.toWholePercent()
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            R.string.risk_stop_loss,
                            riskConfig.config.stopLossPercent.toWholePercent()
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            R.string.risk_take_profit,
                            riskConfig.config.takeProfitPercent.toWholePercent()
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            R.string.risk_max_capital_usage,
                            riskConfig.config.maxCapitalUsage.toWholePercent()
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            R.string.risk_max_positions,
                            riskConfig.config.maxPositions
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            if (riskConfig.config.shortTradingEnabled) {
                                R.string.risk_short_trading_enabled
                            } else {
                                R.string.risk_short_trading_disabled
                            }
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

private fun Double.toWholePercent(): Int = (this * 100).roundToInt()

@Composable
private fun PositionItem(position: Position) {
    val pnl = position.pnl
    val isPositive = pnl != null && pnl >= 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = position.instrumentName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        if (position.positionSide == "SHORT") {
                            R.string.dashboard_position_side_short
                        } else {
                            R.string.dashboard_position_side_long
                        }
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                position.entryStrategyName
                    ?.takeIf(String::isNotBlank)
                    ?.let { strategyName ->
                        Text(
                            text = stringResource(
                                R.string.dashboard_position_strategy,
                                strategyName
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                Text(
                    text = "${position.quantity} шт. по ${
                        String.format(
                            "%.2f",
                            position.entryPrice
                        )
                    } ₽",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.2f, false),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = pnl?.let { value ->
                        "${if (isPositive) "+" else ""}${String.format("%.2f", value)} ₽"
                    } ?: "—",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        pnl == null -> MaterialTheme.colorScheme.onSurfaceVariant
                        isPositive -> Color(0xFF4CAF50)
                        else -> Color(0xFFF44336)
                    }
                )
            }
        }
    }
}

// НОВОЕ: Chip для отображения текущего режима рынка
@Composable
private fun MarketRegimeChip(regime: MarketRegime) {
    val (label, containerColor, contentColor) = when (regime) {
        MarketRegime.STRONG_UPTREND -> Triple("📈 Сильный рост", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        MarketRegime.STRONG_DOWNTREND -> Triple("📉 Сильное падение", Color(0xFFFFEBEE), Color(0xFFC62828))
        MarketRegime.WEAK_TREND -> Triple("📊 Слабый тренд", Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        MarketRegime.FLAT_LOW_VOL -> Triple("➡️ Тихий боковик", Color(0xFFE0E0E0), Color(0xFF616161))
        MarketRegime.FLAT_HIGH_VOL -> Triple("🌊 Волатильный боковик", Color(0xFFFFF8E1), Color(0xFFF57F17))
        MarketRegime.VOLATILE -> Triple("⚡ Волатильность", Color(0xFFFFF3E0), Color(0xFFE65100))
        MarketRegime.EXTREME_VOLATILE -> Triple("🚫 Экстремальная волатильность", Color(0xFFFFEBEE), Color(0xFFB71C1C))
        MarketRegime.UNCERTAIN -> Triple("❓ Неопределённость", Color(0xFFECEFF1), Color(0xFF455A64))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
            Text(
                text = if (regime.isTradingAllowed()) "Торговля активна" else "Входы заблокированы",
                fontSize = 12.sp,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(48.dp))
            Text(text = message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) { Text("Повторить") }
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(name = "Dashboard - Loading", showBackground = true)
@Composable
private fun PreviewDashboardLoading() {
    MaterialTheme { DashboardScreenPreview(uiState = UiState(isLoading = true)) }
}

@Preview(name = "Dashboard - Success", showBackground = true)
@Composable
private fun PreviewDashboardSuccess() {
    MaterialTheme {
        DashboardScreenPreview(
            uiState = UiState(
                isLoading = false,
                botStatus = BotStatus(
                    running = true,
                    currentStrategy = CurrentStrategy(
                        id = "candlestick",
                        name = "CandlestickPatterns",
                        description = "Стратегия на основе свечных паттернов",
                        type = "candlestick",
                        settings = StrategySettings(
                            timeframe = "M5",
                            minConfidence = 0.85
                        )
                    ),
                    activeInstruments = listOf("SBER", "YDEX", "T")
                ),
                positions = listOf(
                    Position(
                        positionId = "1",
                        instrumentId = "sber",
                        instrumentName = "SBER",
                        direction = "BUY",
                        entryPrice = 320.27,
                        quantity = 138,
                        entryTime = null,
                        stopLossPrice = null,
                        atr = null,
                        pnl = 64.86,
                        currentPrice = null
                    ),
                    Position(
                        positionId = "2",
                        instrumentId = "yd",
                        instrumentName = "YDEX",
                        direction = "BUY",
                        entryPrice = 4024.5,
                        quantity = 185,
                        entryTime = null,
                        stopLossPrice = null,
                        atr = null,
                        pnl = 23032.5,
                        currentPrice = null
                    ),
                    Position(
                        positionId = "3",
                        instrumentId = "prmd",
                        instrumentName = "PRMD",
                        direction = "SELL",
                        entryPrice = 399.9,
                        quantity = 100,
                        entryTime = null,
                        stopLossPrice = null,
                        atr = null,
                        pnl = -2240.0,
                        currentPrice = null
                    )
                ),
                totalPnl = 20857.36,
                dailyPnl = 23032.5,
                winRate = 66.7,
                riskConfig = RiskConfig(
                    success = true,
                    config = RiskConfigData(
                        positionSizePercent = 0.05,
                        positionSizePercentDisplay = "5%",
                        stopLossPercent = 0.02,
                        stopLossPercentDisplay = "2%",
                        takeProfitPercent = 0.03,
                        takeProfitPercentDisplay = "3%",
                        maxCapitalUsage = 0.8,
                        maxCapitalUsagePercent = "80%",
                        maxPositions = 10
                    )
                )
            )
        )
    }
}

@Preview(name = "Dashboard - Error", showBackground = true)
@Composable
private fun PreviewDashboardError() {
    MaterialTheme {
        DashboardScreenPreview(
            uiState = UiState(
                isLoading = false,
                errorMessage = "Не удалось загрузить данные. Проверьте соединение."
            )
        )
    }
}

@Preview(name = "Dashboard - Empty", showBackground = true)
@Composable
private fun PreviewDashboardEmpty() {
    MaterialTheme {
        DashboardScreenPreview(
            uiState = UiState(
                isLoading = false,
                botStatus = BotStatus(
                    running = false,
                    currentStrategy = CurrentStrategy(
                        id = "ema",
                        name = "EMA",
                        description = "Cross EMA Strategy",
                        type = "simple",
                        settings = StrategySettings()
                    ),
                    activeInstruments = emptyList()
                ),
                positions = emptyList(),
                totalPnl = 0.0,
                dailyPnl = 0.0,
                winRate = 0.0,
                riskConfig = RiskConfig(
                    success = true,
                    config = RiskConfigData(
                        positionSizePercent = 0.05,
                        positionSizePercentDisplay = "5%",
                        stopLossPercent = 0.02,
                        stopLossPercentDisplay = "2%",
                        takeProfitPercent = 0.03,
                        takeProfitPercentDisplay = "3%",
                        maxCapitalUsage = 0.8,
                        maxCapitalUsagePercent = "80%",
                        maxPositions = 10
                    )
                )
            )
        )
    }
}

@Composable
private fun DashboardScreenPreview(
    uiState: UiState,
    onNavigateToRisk: () -> Unit = {},
    onNavigateToPositions: () -> Unit = {},
    onNavigateToStrategy: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            DashboardTopBar(
                isRunning = uiState.botStatus?.running ?: false,
                allTradingUnavailable = uiState.botStatus?.allTradingUnavailable ?: false
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when {
                uiState.errorMessage != null -> ErrorScreen(uiState.errorMessage!!) {}
                else -> DashboardContent(
                    state = uiState,
                    onStartBot = {},
                    onStopBot = {},
                    onNavigateToStrategy = onNavigateToStrategy,
                    onNavigateToRisk = onNavigateToRisk,
                    onNavigateToPositions = onNavigateToPositions,
                    onShowAllPositions = onNavigateToPositions
                )
            }
        }
    }
}

@Composable
private fun InstrumentsInfoCard(count: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Инструменты бота",
                    fontWeight = FontWeight.SemiBold
                ); Text(
                "Выбрано для торговли: $count",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}
