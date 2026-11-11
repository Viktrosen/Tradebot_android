package ru.bolotov.tradebot.di.components

import dagger.Component
import ru.bolotov.feature.auth.presentation.AuthDependencies
import ru.bolotov.tradebot.navigation.AppViewModel

@Component(dependencies = [AuthDependencies::class])
internal interface AppNavigationComponent {
    val viewModel: AppViewModel

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: AuthDependencies): Builder
        fun build(): AppNavigationComponent
    }
}
