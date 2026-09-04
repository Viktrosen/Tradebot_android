package ru.bolotov.feature.main.presentation.di

import dagger.Component

@Component
interface HomeFeatureComponent {
    @Component.Builder
    interface Builder {
        fun build(): HomeFeatureComponent
    }
}