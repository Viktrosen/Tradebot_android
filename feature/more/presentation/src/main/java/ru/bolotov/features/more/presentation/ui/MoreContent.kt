package ru.bolotov.features.more.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.*
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.features.more.presentation.core.Action
import ru.bolotov.features.more.presentation.core.MoreMenuItem
import ru.bolotov.features.more.presentation.core.ThemeMode
import ru.bolotov.features.more.presentation.core.UiState
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.more.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoreContent(
    state: UiState,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TradebotHeader(
                stringResource(R.string.more_title),
                stringResource(R.string.more_subtitle),
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { onAction(Action.LoadData) }) {
                                Text(stringResource(UiR.string.tb_retry))
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(TradebotDimens.screenPadding),
                        verticalArrangement = Arrangement.spacedBy(TradebotDimens.largeGap),
                    ) {
                        // User info section
                        item { UserInfoCard(email = state.email) }

                        item { TradebotSectionTitle(stringResource(R.string.more_preferences)) }

                        // Notifications toggle
                        item {
                            SettingsSwitchItem(
                                title = stringResource(R.string.more_push),
                                checked = state.notificationsEnabled,
                                onCheckedChange = { onAction(Action.SetNotificationsEnabled(it)) },
                            )
                        }

                        // Theme selection
                        item {
                            ThemeSelectionItem(
                                currentTheme = state.themeMode,
                                onThemeSelected = { onAction(Action.SetThemeMode(it)) },
                            )
                        }

                        // Menu items
                        item { TradebotSectionTitle(stringResource(R.string.more_navigation)) }

                        items(menuItems().filter { it.id == "instruments" }) { menuItem ->
                            MoreMenuItem(
                                item = menuItem,
                                onClick = { onAction(Action.NavigateTo(menuItem.id)) },
                            )
                        }

                        item {
                            EmergencyCloseCard(
                                isInProgress = state.isEmergencyCloseInProgress,
                                onClick = { onAction(Action.ShowEmergencyCloseDialog) },
                            )
                        }

                        item { TradebotSectionTitle(stringResource(R.string.more_application)) }

                        items(menuItems().filter { it.id != "instruments" }) { menuItem ->
                            MoreMenuItem(
                                item = menuItem,
                                onClick = { onAction(Action.NavigateTo(menuItem.id)) },
                            )
                        }

                        // Version info
                        item {
                            VersionInfo(version = state.appVersion, buildNumber = state.buildNumber)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyCloseCard(isInProgress: Boolean, onClick: () -> Unit) {
    TradebotCard(
        Modifier.fillMaxWidth(),
        shape = TradebotTheme.shapes.clipped,
        containerColor = TradebotTheme.colors.dangerSurface,
        borderColor = TradebotTheme.colors.loss,
    ) {
        Column(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
            ) {
                TradebotIcon(UiR.drawable.ic_warning_outline, tint = TradebotTheme.colors.loss)
                Text(
                    stringResource(R.string.emergency_close_title),
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(
                stringResource(R.string.emergency_close_description),
                style = MaterialTheme.typography.bodyMedium,
                color = TradebotTheme.colors.textSecondary,
            )
            TradebotButton(
                stringResource(
                    if (isInProgress) R.string.emergency_close_in_progress
                    else R.string.emergency_close_action
                ),
                onClick,
                Modifier.fillMaxWidth(),
                enabled = !isInProgress,
                destructive = true,
            )
            Text(
                stringResource(R.string.more_confirmation_hint),
                style = MaterialTheme.typography.bodySmall,
                color = TradebotTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
internal fun EmergencyCloseDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    TradebotConfirmationDialog(
        title = stringResource(R.string.emergency_close_dialog_title),
        message = stringResource(R.string.emergency_close_dialog_message),
        confirmText = stringResource(R.string.emergency_close_confirm),
        dismissText = stringResource(R.string.emergency_close_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        destructive = true,
    )
}

private fun menuItems(): List<MoreMenuItem> =
    listOf(
        MoreMenuItem.Instruments,
        MoreMenuItem.History,
        MoreMenuItem.Settings,
        MoreMenuItem.About,
        MoreMenuItem.Logout,
    )

@Preview(name = "More · preferences", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun MoreContentPreview() {
    TradebotTheme {
        MoreContent(UiState(email = "user@example.com", themeMode = ThemeMode.DARK), onAction = {})
    }
}

@Preview
@Composable
private fun EmergencyCloseDialogPreview() {
    TradebotTheme { EmergencyCloseDialog(onConfirm = {}, onDismiss = {}) }
}
