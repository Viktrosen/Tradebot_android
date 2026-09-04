package ru.bolotov.feature.instruments.presentation.di

import dagger.Component
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.feature.instruments.presentation.core.InstrumentsViewModel

@SubFeatureScoped
@Component(dependencies = [InstrumentsDependencies::class, InstrumentsFeatureComponent::class])
internal interface InstrumentsComponent {
    @Component.Builder interface Builder { fun component(component: InstrumentsFeatureComponent): Builder; fun dependencies(dependencies: InstrumentsDependencies): Builder; fun build(): InstrumentsComponent }
    val instrumentsViewModel: InstrumentsViewModel
}

@Component
interface InstrumentsFeatureComponent { @Component.Builder interface Builder { fun build(): InstrumentsFeatureComponent } }
