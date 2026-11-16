package ru.bolotov.tradebot.di.components

import androidx.lifecycle.SavedStateHandle
import dagger.BindsInstance
import dagger.Component
import ru.bolotov.tradebot.navigation.AppViewModel

@Component(dependencies = [AppNavigationDependencies::class])
internal interface AppNavigationComponent {
    val viewModel: AppViewModel

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: AppNavigationDependencies): Builder

        @BindsInstance fun savedStateHandle(savedStateHandle: SavedStateHandle): Builder

        fun build(): AppNavigationComponent
    }
}
