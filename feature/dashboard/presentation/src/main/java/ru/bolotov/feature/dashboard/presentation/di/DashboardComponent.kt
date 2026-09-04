package ru.bolotov.feature.dashboard.presentation.di

import dagger.Component
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.feature.dashboard.presentation.core.DashboardViewModel

@SubFeatureScoped
@Component(
    dependencies = [
        DashboardDependencies::class,
        DashboardFeatureComponent::class
    ]
)
internal interface DashboardComponent {
    @Component.Builder
    interface Builder {
        fun component(dependencies: DashboardFeatureComponent): Builder
        fun dependencies(dependencies: DashboardDependencies): Builder
        fun build(): DashboardComponent
    }

    val dashboardViewModel: DashboardViewModel
}