package ru.bolotov.feature.auth.presentation

import dagger.Component
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.feature.auth.domain.AuthInteractor

interface AuthDependencies : Dependencies { val authInteractor: AuthInteractor }

@Component(dependencies = [AuthDependencies::class])
internal interface AuthComponent {
    val viewModel: AuthViewModel
    @Component.Builder interface Builder {
        fun dependencies(dependencies: AuthDependencies): Builder
        fun build(): AuthComponent
    }
}
