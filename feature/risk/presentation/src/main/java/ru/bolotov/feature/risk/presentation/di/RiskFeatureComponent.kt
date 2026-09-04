package ru.bolotov.feature.risk.presentation.di

import dagger.Component

@Component
interface RiskFeatureComponent {
    @Component.Builder
    interface Builder {
        fun build(): RiskFeatureComponent
    }
}