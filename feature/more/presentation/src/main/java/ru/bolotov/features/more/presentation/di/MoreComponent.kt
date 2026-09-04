package ru.bolotov.features.more.presentation.di

import dagger.Component
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.features.more.presentation.core.MoreViewModel

@SubFeatureScoped
@Component(
    dependencies = [
        MoreDependencies::class,
        MoreFeatureComponent::class
    ]
)
internal interface MoreComponent {
    @Component.Builder
    interface Builder {
        fun component(dependencies: MoreFeatureComponent): Builder
        fun dependencies(dependencies: MoreDependencies): Builder
        fun build(): MoreComponent
    }

    val moreViewModel: MoreViewModel
}