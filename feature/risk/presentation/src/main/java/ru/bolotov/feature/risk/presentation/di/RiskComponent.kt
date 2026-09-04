package ru.bolotov.feature.risk.presentation.di

import dagger.Component
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.feature.risk.presentation.core.RiskViewModel

@SubFeatureScoped
@Component(
    dependencies = [
        RiskDependencies::class,
        RiskFeatureComponent::class
    ]
)
internal interface RiskComponent {
    @Component.Builder
    interface Builder {
        fun component(dependencies: RiskFeatureComponent): Builder
        fun dependencies(dependencies: RiskDependencies): Builder
        fun build(): RiskComponent
    }

    val riskViewModel: RiskViewModel
}