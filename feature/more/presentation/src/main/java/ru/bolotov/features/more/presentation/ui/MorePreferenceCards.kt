package ru.bolotov.features.more.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.*
import ru.bolotov.core.uikit.components.TradebotSwitch
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.features.more.presentation.core.ThemeMode
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.more.presentation.R

@Composable
internal fun UserInfoCard(email: String) {
    TradebotCard(Modifier.fillMaxWidth(), shape = TradebotTheme.shapes.clipped) {
        Row(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TradebotDimens.largeGap),
        ) {
            Surface(
                shape = TradebotTheme.shapes.clipped,
                color = TradebotTheme.colors.accent.copy(alpha = 0.06f),
                border = BorderStroke(1.dp, TradebotTheme.colors.accent),
            ) {
                TradebotIcon(
                    UiR.drawable.ic_person_outline,
                    Modifier.padding(12.dp).size(28.dp),
                    tint = TradebotTheme.colors.accent,
                )
            }
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(TradebotDimens.smallGap),
            ) {
                Text(
                    stringResource(R.string.more_user),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    email,
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
internal fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    TradebotCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            TradebotIcon(UiR.drawable.ic_bell_outline)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(R.string.more_push_detail),
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.textSecondary,
                )
            }
            TradebotSwitch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
internal fun ThemeSelectionItem(currentTheme: ThemeMode, onThemeSelected: (ThemeMode) -> Unit) {
    TradebotCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.largeGap),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TradebotIcon(UiR.drawable.ic_settings_outline)
                Text(
                    stringResource(R.string.more_appearance),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap)) {
                ThemeMode.entries.forEach { mode ->
                    val selected = mode == currentTheme
                    val (title, icon) =
                        when (mode) {
                            ThemeMode.LIGHT ->
                                stringResource(R.string.more_light) to UiR.drawable.ic_sun_outline
                            ThemeMode.DARK ->
                                stringResource(R.string.more_dark) to UiR.drawable.ic_moon_outline
                            ThemeMode.SYSTEM ->
                                stringResource(R.string.more_system) to
                                    UiR.drawable.ic_system_outline
                        }
                    val color =
                        if (selected) TradebotTheme.colors.accent
                        else TradebotTheme.colors.textSecondary
                    Surface(
                        modifier =
                            Modifier.weight(1f)
                                .selectable(
                                    selected = selected,
                                    role = Role.RadioButton,
                                    onClick = { onThemeSelected(mode) },
                                ),
                        shape = TradebotTheme.shapes.card,
                        border =
                            BorderStroke(
                                1.dp,
                                if (selected) color else TradebotTheme.colors.border,
                            ),
                        color =
                            if (selected) color.copy(alpha = 0.08f)
                            else TradebotTheme.colors.surface,
                    ) {
                        Column(
                            Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
                        ) {
                            TradebotIcon(icon, tint = color)
                            Text(
                                title,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) TradebotTheme.colors.text else color,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}
