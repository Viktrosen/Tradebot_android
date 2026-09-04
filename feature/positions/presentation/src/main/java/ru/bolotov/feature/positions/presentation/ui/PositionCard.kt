package ru.bolotov.feature.positions.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.bolotov.feature.positions.presentation.core.UiPosition
import ru.bolotov.tradebot.feature.positions.presentation.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PositionCard(
    position: UiPosition,
    onCloseClick: () -> Unit,
    onInstrumentClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onInstrumentClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (position.isClosed)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Левая часть: информация об инструменте
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = position.instrumentName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    if (!position.infoExplanation.isNullOrBlank()) {
                        IconButton(onClick = onInfoClick) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(
                                    if (position.isClosed) {
                                        R.string.position_close_reason_title
                                    } else {
                                        R.string.position_ai_explanation_title
                                    }
                                )
                            )
                        }
                    }
                }

                // Quantity and prices
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(
                            if (position.positionSide == "SHORT") {
                                R.string.position_side_short
                            } else {
                                R.string.position_side_long
                            }
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (!position.isClosed && !position.entryStrategyName.isNullOrBlank()) {
                        Text(
                            text = stringResource(
                                R.string.position_entry_strategy,
                                position.entryStrategyName
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${position.quantity} лотов",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Вход: ${String.format("%.2f", position.entryPrice)} ₽",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!position.isClosed && position.currentPrice != null) {
                        Text(
                            text = "Текущая: ${String.format("%.2f", position.currentPrice)} ₽",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Time info
                if (position.entryTime != null) {
                    Text(
                        text = "Открыта: ${position.entryTime.toPositionDateTime()}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (position.isClosed && position.closeTime != null) {
                    Text(
                        text = "Закрыта: ${position.closeTime.toPositionDateTime()}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Правая часть: P&L и кнопка закрытия
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // P&L values
                if (position.pnl != null) {
                    val isPositive = position.pnl > 0
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.Close else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format("%.2f", position.pnl)} ₽",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }

                    if (position.pnlPercent != null) {
                        Text(
                            text = "${if (position.pnlPercent > 0) "+" else ""}${String.format("%.2f", position.pnlPercent)}%",
                            fontSize = 12.sp,
                            color = if (position.pnlPercent > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                }

                // Close button (only for open positions)
                if (!position.isClosed) {
                    Button(
                        onClick = onCloseClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.position_close),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.position_close), fontSize = 12.sp)
                    }
                } else {
                    Text(
                        text = stringResource(R.string.position_closed),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun String.toPositionDateTime(): String = runCatching {
    Instant.parse(this).atZone(ZoneId.systemDefault()).format(POSITION_DATE_TIME_FORMATTER)
}.getOrElse { this }

private val POSITION_DATE_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
