package ru.bolotov.features.more.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.features.more.presentation.core.Action
import ru.bolotov.features.more.presentation.core.Effect
import ru.bolotov.features.more.presentation.core.MoreMenuItem
import ru.bolotov.features.more.presentation.core.MoreViewModel
import ru.bolotov.features.more.presentation.core.ThemeMode
import ru.bolotov.tradebot.feature.more.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoreScreen(
    viewModel: MoreViewModel,
    onNavigateToInstruments: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.ShowToast -> {
                // TODO: Show toast implementation
            }
            is Effect.ShowMessage -> {
                snackbarHostState.showSnackbar(context.getString(effect.messageResId))
            }
            is Effect.ShowError -> {
                // TODO: Show error dialog
            }
            is Effect.ShowApiKeyDialog -> {
                showApiKeyDialog = true
            }
            is Effect.DismissApiKeyDialog -> {
                showApiKeyDialog = false
            }
            is Effect.NavigateToInstruments -> onNavigateToInstruments()
            is Effect.NavigateToHistory -> onNavigateToHistory()
            is Effect.NavigateToSettings -> onNavigateToSettings()
            is Effect.NavigateToAbout -> onNavigateToAbout()
            is Effect.Logout -> onLogout()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ещё") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
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
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // User info section
                        item {
                            UserInfoCard(email = "user@example.com")
                        }

                        // API Key section
                        item {
                            ApiKeyCard(
                                isConfigured = state.isApiKeySaved,
                                onConfigure = { viewModel.handleAction(Action.ShowApiKeyDialog) }
                            )
                        }

                        // Notifications toggle
                        item {
                            SettingsSwitchItem(
                                title = "Push-уведомления",
                                checked = state.notificationsEnabled,
                                onCheckedChange = { viewModel.handleAction(Action.SetNotificationsEnabled(it)) }
                            )
                        }

                        // Theme selection
                        item {
                            ThemeSelectionItem(
                                currentTheme = state.themeMode,
                                onThemeSelected = { viewModel.handleAction(Action.SetThemeMode(it)) }
                            )
                        }

                        item {
                            EmergencyCloseCard(
                                isInProgress = state.isEmergencyCloseInProgress,
                                onClick = {
                                    viewModel.handleAction(Action.ShowEmergencyCloseDialog)
                                }
                            )
                        }

                        // Menu items
                        item {
                            Text(
                                text = "Навигация",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(menuItems()) { menuItem ->
                            MoreMenuItem(
                                item = menuItem,
                                onClick = { viewModel.handleAction(Action.NavigateTo(menuItem.id)) }
                            )
                        }

                        // Version info
                        item {
                            VersionInfo(
                                version = state.appVersion,
                                buildNumber = state.buildNumber
                            )
                        }
                    }
                }
            }
        }
    }

    // API Key dialog
    if (showApiKeyDialog) {
        ApiKeyDialog(
            currentKey = state.apiKey,
            onSave = { viewModel.handleAction(Action.SaveApiKey(it)) },
            onDismiss = { viewModel.handleAction(Action.DismissApiKeyDialog) }
        )
    }

    if (state.isEmergencyCloseDialogVisible) {
        EmergencyCloseDialog(
            onConfirm = { viewModel.handleAction(Action.ConfirmEmergencyClose) },
            onDismiss = { viewModel.handleAction(Action.DismissEmergencyCloseDialog) }
        )
    }
}

@Composable
private fun EmergencyCloseCard(
    isInProgress: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.emergency_close_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = stringResource(R.string.emergency_close_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Button(
                onClick = onClick,
                enabled = !isInProgress,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                if (isInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onError,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.emergency_close_in_progress))
                } else {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.emergency_close_action))
                }
            }
        }
    }
}

@Composable
private fun EmergencyCloseDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.emergency_close_dialog_title)) },
        text = { Text(stringResource(R.string.emergency_close_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.emergency_close_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.emergency_close_cancel))
            }
        }
    )
}

private fun menuItems(): List<MoreMenuItem> = listOf(
    MoreMenuItem.Instruments,
    MoreMenuItem.History,
    MoreMenuItem.Settings,
    MoreMenuItem.About,
    MoreMenuItem.Logout
)

@Composable
private fun UserInfoCard(email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "TradeBot User",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ApiKeyCard(
    isConfigured: Boolean,
    onConfigure: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onConfigure
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,  // ← VpnKey → Lock
                    contentDescription = null,
                    tint = if (isConfigured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "API ключ",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isConfigured) "Ключ настроен" else "Ключ не настроен",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isConfigured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,  // ← ChevronRight → KeyboardArrowRight
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun ThemeSelectionItem(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,  // ← BrightnessMedium → Settings
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Тема",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.values().forEach { mode ->
                    FilterChip(
                        selected = currentTheme == mode,
                        onClick = { onThemeSelected(mode) },
                        label = {
                            Text(
                                when (mode) {
                                    ThemeMode.LIGHT -> "Светлая"
                                    ThemeMode.DARK -> "Тёмная"
                                    ThemeMode.SYSTEM -> "Системная"
                                }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
