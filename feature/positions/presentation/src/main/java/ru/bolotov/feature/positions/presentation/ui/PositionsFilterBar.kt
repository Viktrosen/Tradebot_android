package ru.bolotov.feature.positions.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.feature.positions.presentation.core.PositionFilter
import ru.bolotov.tradebot.feature.positions.presentation.R

@Composable
fun PositionsFilterBar(
    currentFilter: PositionFilter,
    onFilterSelected: (PositionFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        selectedTabIndex = currentFilter.ordinal,
        containerColor = MaterialTheme.colorScheme.background,
        edgePadding = TradebotDimens.screenPadding,
        modifier = modifier.fillMaxWidth(),
    ) {
        PositionFilter.values().forEach { filter ->
            val isSelected = currentFilter == filter
            Tab(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                text = {
                    Text(
                        maxLines = 1,
                        softWrap = false,
                        text =
                            when (filter) {
                                PositionFilter.ALL -> stringResource(R.string.positions_all)
                                PositionFilter.OPEN -> stringResource(R.string.positions_open)
                                PositionFilter.PROFIT -> stringResource(R.string.positions_profit)
                                PositionFilter.LOSS -> stringResource(R.string.positions_loss)
                            },
                        color =
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
    }
}
