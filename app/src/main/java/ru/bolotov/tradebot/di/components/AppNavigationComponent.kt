package ru.bolotov.tradebot.di.components

import dagger.Component
import ru.bolotov.tradebot.navigation.AppViewModel

@Component(dependencies = [AppNavigationDependencies::class])
internal interface AppNavigationComponent {
    val viewModel: AppViewModel

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: AppNavigationDependencies): Builder

        fun build(): AppNavigationComponent
    }
}
