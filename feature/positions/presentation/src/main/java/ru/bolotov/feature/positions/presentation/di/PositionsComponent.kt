package ru.bolotov.feature.positions.presentation.di

import dagger.Component
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.feature.positions.presentation.core.PositionsViewModel

@SubFeatureScoped
@Component(
    dependencies = [
        PositionsDependencies::class,
        PositionsFeatureComponent::class
    ]
)
internal interface PositionsComponent {
    @Component.Builder
    interface Builder {
        fun component(dependencies: PositionsFeatureComponent): Builder
        fun dependencies(dependencies: PositionsDependencies): Builder
        fun build(): PositionsComponent
    }

    val positionsViewModel: PositionsViewModel
}