package ru.bolotov.feature.auth.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ru.bolotov.feature.auth.domain.model.AuthSession
import ru.bolotov.feature.auth.presentation.core.Action
import ru.bolotov.feature.auth.presentation.core.UiState

@Composable
internal fun AuthScreen(state: UiState, onAction: (Action) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tradebot", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(24.dp))
            when (state.session) {
                AuthSession.Checking -> {
                    CircularProgressIndicator()
                    Text("Проверяем сессию")
                }

                AuthSession.Unavailable -> {
                    Text("Не удалось проверить сессию. Проверьте подключение к серверу.")
                    Button(onClick = { onAction(Action.Retry) }) { Text("Повторить") }
                    TextButton(onClick = { onAction(Action.Logout) }) { Text("Выйти из аккаунта") }
                }

                else -> {
                    Text("Вход в аккаунт", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.email, onValueChange = { onAction(Action.Email(it)) },
                        label = { Text("Почта") }, enabled = !state.loading, singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentType = ContentType.EmailAddress }
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.password, onValueChange = { onAction(Action.Password(it)) },
                        label = { Text("Пароль") }, enabled = !state.loading, singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onAction(Action.Login) }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentType = ContentType.Password }
                    )
                    state.error?.let {
                        Spacer(Modifier.height(12.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { onAction(Action.Login) },
                        enabled = !state.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.loading) CircularProgressIndicator(
                            Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        else Text("Войти")
                    }
                }
            }
        }
    }
}
