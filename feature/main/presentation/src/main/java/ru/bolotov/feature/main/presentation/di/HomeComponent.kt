package ru.bolotov.feature.main.presentation.di

import dagger.Component
import ru.bolotov.feature.main.presentation.core.MainHostViewModel
import ru.bolotov.core.dependency.FeatureScoped

@FeatureScoped
@Component(dependencies = [MainDependencies::class, ])
internal interface HomeComponent {
    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: MainDependencies): Builder
        fun build(): HomeComponent
    }

    val viewModel: MainHostViewModel
}