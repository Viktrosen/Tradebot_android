package ru.bolotov.features.more.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.bolotov.core.uikit.components.TradebotNavigationRow
import ru.bolotov.features.more.presentation.core.MoreMenuItem

@Composable
fun MoreMenuItem(item: MoreMenuItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TradebotNavigationRow(
        title = stringResource(item.titleRes),
        icon = item.iconRes,
        onClick = onClick,
        modifier = modifier
    )
}
