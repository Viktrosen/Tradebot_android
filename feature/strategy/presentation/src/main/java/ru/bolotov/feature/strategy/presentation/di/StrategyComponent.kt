package ru.bolotov.feature.strategy.presentation.di

import dagger.Component
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.feature.strategy.presentation.core.StrategyViewModel

@SubFeatureScoped
@Component(
    dependencies = [
        StrategyDependencies::class,
        StrategyFeatureComponent::class
    ]
)
internal interface StrategyComponent {
    @Component.Builder
    interface Builder {
        fun component(dependencies: StrategyFeatureComponent): Builder
        fun dependencies(dependencies: StrategyDependencies): Builder
        fun build(): StrategyComponent
    }

    val strategyViewModel: StrategyViewModel
}