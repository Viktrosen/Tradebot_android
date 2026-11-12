package ru.bolotov.tradebot.di.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import ru.bolotov.feature.more.data.local.themeDataStore
import ru.bolotov.feature.more.domain.core.ThemeInteractorImpl
import ru.bolotov.feature.more.domain.interactor.ThemeInteractor
import ru.bolotov.feature.more.domain.repository.ThemeRepository

@Module
class ThemeModule {
    @Provides
    @Singleton
    fun provideThemeDataStore(context: Context): DataStore<Preferences> = context.themeDataStore

    @Provides
    @Singleton
    fun provideThemeInteractor(repository: ThemeRepository): ThemeInteractor =
        ThemeInteractorImpl(repository)
}
