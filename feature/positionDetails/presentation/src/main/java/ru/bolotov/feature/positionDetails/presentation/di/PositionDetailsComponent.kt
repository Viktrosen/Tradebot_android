package ru.bolotov.feature.positionDetails.presentation.di

import androidx.lifecycle.SavedStateHandle
import dagger.BindsInstance
import dagger.Component
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.feature.positionDetails.presentation.core.PositionDetailsViewModel

@SubFeatureScoped
@Component(
    dependencies = [PositionDetailsDependencies::class, PositionDetailsFeatureComponent::class]
)
internal interface PositionDetailsComponent {
    val viewModel: PositionDetailsViewModel

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: PositionDetailsDependencies): Builder

        fun feature(feature: PositionDetailsFeatureComponent): Builder

        @BindsInstance fun positionId(positionId: String): Builder

        @BindsInstance fun savedStateHandle(savedStateHandle: SavedStateHandle): Builder

        fun build(): PositionDetailsComponent
    }
}
