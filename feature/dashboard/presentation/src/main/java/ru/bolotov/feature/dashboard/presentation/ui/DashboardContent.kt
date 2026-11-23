package ru.bolotov.feature.dashboard.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.*
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.core.uikit.format.formatMoney
import ru.bolotov.core.uikit.format.formatPercent
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.CurrentStrategy
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.RiskConfig
import ru.bolotov.feature.dashboard.domain.model.RiskConfigData
import ru.bolotov.feature.dashboard.domain.model.StrategySettings
import ru.bolotov.feature.dashboard.presentation.core.UiState
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.dashboard.presentation.R

@Composable
internal fun DashboardContent(
    state: UiState,
    onStartBot: () -> Unit,
    onStopBot: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToRisk: () -> Unit,
    onNavigateToPositions: () -> Unit,
    onNavigateToInstruments: () -> Unit,
    onShowAllPositions: () -> Unit,
    onOpenPositionDetails: (String) -> Unit = {}
) {
    val colors = TradebotTheme.colors
    val running = state.botStatus?.running == true
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(TradebotDimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    Modifier.size(10.dp)
                        .background(if (running) colors.accent else colors.loss, CircleShape)
                )
                Text(
                    if (running) stringResource(R.string.dashboard_bot_running)
                    else stringResource(R.string.dashboard_bot_stopped),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    if (state.botStatus?.allTradingUnavailable == true)
                        stringResource(R.string.dashboard_unavailable)
                    else stringResource(R.string.dashboard_available),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
            ) {
                TradebotMetric(
                    label = stringResource(R.string.dashboard_total_pnl),
                    value = state.totalPnl.money(),
                    modifier = Modifier.weight(1f),
                    color = pnlColor(state.totalPnl)
                )
                TradebotMetric(
                    label = stringResource(R.string.dashboard_today),
                    value = state.dailyPnl.money(),
                    modifier = Modifier.weight(1f),
                    color = pnlColor(state.dailyPnl)
                )
                TradebotMetric(
                    label = stringResource(R.string.dashboard_win_rate),
                    value = formatPercent(state.winRate, digits = 1),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            TradebotCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(TradebotDimens.cardPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TradebotDimens.largeGap)
                ) {
                    TradebotIcon(UiR.drawable.ic_wallet_outline)
                    Column {
                        Text(
                            stringResource(R.string.dashboard_free_cash),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Text(state.availableCash.money(), style = TradebotTheme.typography.amount)
                    }
                }
            }
        }
        if (state.marketRegime?.isWarning() == true) {
            item {
                TradebotCard(
                    Modifier.fillMaxWidth(),
                    containerColor = colors.dangerSurface,
                    borderColor = colors.loss
                ) {
                    Text(
                        stringResource(R.string.dashboard_warning),
                        Modifier.padding(TradebotDimens.cardPadding),
                        color = colors.loss
                    )
                }
            }
        }
        item {
            TradebotSectionTitle(
                stringResource(R.string.dashboard_control_section),
                Modifier.padding(top = 8.dp),
            )
        }
        item {
            TradebotCard(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(TradebotDimens.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TradebotIcon(UiR.drawable.ic_bot_outline)
                        Text(
                            stringResource(R.string.dashboard_control_title),
                            Modifier.weight(1f).padding(start = 10.dp),
                            style = MaterialTheme.typography.titleSmall
                        )
                        TextButton(onClick = onNavigateToStrategy) {
                            Text(
                                stringResource(R.string.dashboard_strategies_link),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap)) {
                        TradebotButton(
                            stringResource(R.string.dashboard_start),
                            onStartBot,
                            Modifier.weight(1f),
                            enabled = !running,
                            iconRes = UiR.drawable.ic_play_outline
                        )
                        TradebotButton(
                            stringResource(R.string.dashboard_stop),
                            onStopBot,
                            Modifier.weight(1f),
                            enabled = running,
                            outlined = true,
                            destructive = true,
                            iconRes = UiR.drawable.ic_close_24
                        )
                    }
                }
            }
        }
        item { DashboardRiskCard(state.riskConfig, onNavigateToRisk) }
        item {
            TradebotNavigationRow(
                stringResource(R.string.dashboard_instruments),
                UiR.drawable.ic_candles_outline,
                onNavigateToInstruments,
                subtitle =
                    stringResource(
                        R.string.dashboard_selected_count,
                        state.botStatus?.activeInstruments?.size ?: 0,
                    )
            )
        }
        if (state.positions.isNotEmpty()) {
            item {
                TradebotSectionTitle(
                    stringResource(R.string.dashboard_open_positions),
                    Modifier.padding(top = 8.dp),
                )
            }
            items(state.positions.take(5), key = { it.positionId }) { position ->
                DashboardPositionCard(position = position, onClick = { onOpenPositionDetails(position.positionId) })
            }
            if (state.positions.size > 5) {
                item {
                    TextButton(onClick = onShowAllPositions, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.dashboard_show_all, state.positions.size))
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardRiskCard(riskConfig: RiskConfig?, onClick: () -> Unit) {
    val config = riskConfig?.config
    TradebotCard(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TradebotIcon(UiR.drawable.ic_settings_outline)
                Text(
                    stringResource(R.string.dashboard_risk_title),
                    Modifier.weight(1f).padding(start = 12.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                TradebotIcon(UiR.drawable.ic_chevron_right)
            }
            if (config == null) {
                Text(
                    stringResource(R.string.dashboard_risk_pending),
                    color = TradebotTheme.colors.textSecondary,
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(TradebotDimens.largeGap)) {
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
                    ) {
                        RiskSummary(
                            stringResource(R.string.dashboard_position_capital),
                            config.positionSizePercentDisplay,
                        )
                        RiskSummary(
                            stringResource(R.string.dashboard_tp),
                            config.takeProfitPercentDisplay,
                        )
                        RiskSummary(
                            stringResource(R.string.dashboard_max_positions),
                            config.maxPositions.toString(),
                        )
                    }
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
                    ) {
                        RiskSummary(
                            stringResource(R.string.dashboard_sl),
                            config.stopLossPercentDisplay,
                        )
                        RiskSummary(
                            stringResource(R.string.dashboard_usage),
                            config.maxCapitalUsagePercent,
                        )
                        RiskSummary(
                            stringResource(R.string.dashboard_shorts),
                            if (config.shortTradingEnabled) stringResource(R.string.dashboard_on)
                            else stringResource(R.string.dashboard_off),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RiskSummary(label: String, value: String) {
    Text(
        stringResource(
            UiR.string.tb_summary,
            label,
            value
        ),
        style = MaterialTheme.typography.bodySmall,
        color = TradebotTheme.colors.textSecondary
    )
}

@Composable
private fun DashboardPositionCard(position: Position, onClick: () -> Unit) {
    TradebotCard(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
            ) {
                Text(
                    position.instrumentName,
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                TradebotBadge(
                    stringResource(
                        if (position.positionSide == "SHORT") UiR.string.tb_short
                        else UiR.string.tb_long
                    ),
                    color =
                        if (position.positionSide == "SHORT") TradebotTheme.colors.short
                        else TradebotTheme.colors.accent,
                )
            }
            position.entryStrategyName?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.textSecondary
                )
            }
            Text(
                stringResource(
                    R.string.dashboard_entry_detail,
                    pluralStringResource(
                        UiR.plurals.tb_lot_quantity,
                        position.quantity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                        position.quantity
                    ),
                    position.entryPrice.money()
                ),
                style = MaterialTheme.typography.bodySmall,
            )
            position.currentPrice?.let {
                Text(
                    stringResource(R.string.dashboard_current, it.money()),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            position.pnl?.let {
                Text(
                    it.money(),
                    color = pnlColor(it),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            position.brokerStopLossPrice?.let {
                Text(
                    stringResource(R.string.dashboard_broker_sl, it.money()),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            position.managedExitPrice?.let {
                Text(
                    stringResource(R.string.dashboard_profit_protection, it.money()),
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.accent
                )
            }
        }
    }
}

@Composable
private fun pnlColor(value: Double?) =
    when {
        value != null && value > 0 -> TradebotTheme.colors.profit
        value != null && value < 0 -> TradebotTheme.colors.loss
        else -> TradebotTheme.colors.text
    }

@Composable private fun Double?.money() = formatMoney(this)

@Preview(
    name = "Dashboard · running",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun DashboardContentPreview() {
    TradebotTheme {
        Scaffold(
            topBar = {
                TradebotHeader(
                    stringResource(R.string.dashboard_title),
                    stringResource(R.string.dashboard_subtitle),
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                DashboardContent(
                    state =
                        UiState(
                            botStatus =
                                BotStatus(
                                    running = true,
                                    currentStrategy = CurrentStrategy(
                                        "auto",
                                        "Автоматически",
                                        "",
                                        "auto",
                                        StrategySettings()
                                    ),
                                    activeInstruments = List(6) { "preview-$it" }
                                ),
                            availableCash = 1790.76,
                            riskConfig =
                                RiskConfig(
                                    true,
                                    RiskConfigData(
                                        positionSizePercent = 0.02,
                                        positionSizePercentDisplay = "2%",
                                        stopLossPercent = 0.02,
                                        stopLossPercentDisplay = "2%",
                                        takeProfitPercent = 0.03,
                                        takeProfitPercentDisplay = "3%",
                                        maxCapitalUsage = 0.8,
                                        maxCapitalUsagePercent = "80%",
                                        maxPositions = 10
                                    ),
                                )
                        ),
                    onStartBot = {},
                    onStopBot = {},
                    onNavigateToStrategy = {},
                    onNavigateToRisk = {},
                    onNavigateToPositions = {},
                    onNavigateToInstruments = {},
                    onShowAllPositions = {}
                )
            }
        }
    }
}
