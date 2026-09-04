package ru.bolotov.feature.positions.presentation.di

import dagger.Component

@Component
interface PositionsFeatureComponent {
    @Component.Builder
    interface Builder {
        fun build(): PositionsFeatureComponent
    }
}