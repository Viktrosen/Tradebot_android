package ru.bolotov.feature.auth.presentation.di

import dagger.Component
import ru.bolotov.feature.auth.presentation.core.AuthViewModel

@Component(dependencies = [AuthDependencies::class])
internal interface AuthComponent {
    val viewModel: AuthViewModel
    @Component.Builder interface Builder {
        fun dependencies(dependencies: AuthDependencies): Builder
        fun build(): AuthComponent
    }
}
