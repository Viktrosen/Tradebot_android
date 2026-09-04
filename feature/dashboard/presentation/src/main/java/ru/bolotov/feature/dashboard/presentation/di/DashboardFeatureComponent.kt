package ru.bolotov.feature.dashboard.presentation.di

import dagger.Component

@Component
interface DashboardFeatureComponent {
    @Component.Builder
    interface Builder {
        fun build(): DashboardFeatureComponent
    }
}