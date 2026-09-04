package ru.bolotov.features.more.presentation.di

import dagger.Component

@Component
interface MoreFeatureComponent {
    @Component.Builder
    interface Builder {
        fun build(): MoreFeatureComponent
    }
}