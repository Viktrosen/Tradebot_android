package ru.bolotov.tradebot.di.components

import dagger.Component
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.tradebot.navigation.AppEffectsHandlerViewModel

interface AppEffectsHandlerDependencies : Dependencies {
}

@SubFeatureScoped
@Component(
    dependencies = [AppEffectsHandlerDependencies::class]
)
internal interface AppEffectsHandlerComponent {
    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: AppEffectsHandlerDependencies): Builder
        fun build(): AppEffectsHandlerComponent
    }

    val viewModel: AppEffectsHandlerViewModel
}