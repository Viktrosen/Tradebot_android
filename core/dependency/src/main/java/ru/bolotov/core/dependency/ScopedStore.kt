package ru.bolotov.core.dependency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

private class ScopedStore<T : Any>(val value: T) : ViewModel()

@Composable
fun <C : Any> rememberScoped(
    storeOwner: ViewModelStoreOwner,
    key: String? = null,
    builder: () -> C
): C {
    val factory = remember(key) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ScopedStore(builder()) as T
            }
        }
    }
    val store: ScopedStore<C> = viewModel(storeOwner, key, factory)
    return store.value
}