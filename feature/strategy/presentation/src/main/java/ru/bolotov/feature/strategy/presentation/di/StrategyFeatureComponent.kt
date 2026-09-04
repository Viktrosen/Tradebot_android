package ru.bolotov.feature.strategy.presentation.di

import dagger.Component

@Component
interface StrategyFeatureComponent {
    @Component.Builder
    interface Builder {
        fun build(): StrategyFeatureComponent
    }
}