package ru.bolotov.feature.positions.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.bolotov.feature.positions.presentation.core.PositionFilter

@Composable
fun PositionsFilterBar(
    currentFilter: PositionFilter,
    onFilterSelected: (PositionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = currentFilter.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        edgePadding = 16.dp,
        modifier = modifier
    ) {
        PositionFilter.values().forEach { filter ->
            val isSelected = currentFilter == filter
            Tab(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                text = {
                    Text(
                        text = when (filter) {
                            PositionFilter.ALL -> "Все"
                            PositionFilter.OPEN -> "Открытые"
                            PositionFilter.PROFIT -> "Прибыль"
                            PositionFilter.LOSS -> "Убыток"
                        },
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}