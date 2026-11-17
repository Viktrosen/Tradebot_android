package ru.bolotov.tradebot.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.bolotov.tradebot.di.components.AppScope
import ru.bolotov.core.common.ProjectConfigurationInfo
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.core.utils.ResourceProvider
import ru.bolotov.feature.more.domain.core.AppInfoInteractorImpl
import ru.bolotov.feature.more.domain.interactor.AppInfoInteractor
import ru.bolotov.tradebot.BuildConfig
import ru.bolotov.tradebot.environment.ProjectConfigurationInfoCore
import ru.bolotov.tradebot.utils.ResourceProviderCore
import javax.inject.Singleton

@Module
class AppModule {
    @AppScope
    @Provides
    fun provideContext(
        application: Application
    ): Context = application.applicationContext

    @AppScope
    @Provides
    fun provideResourceProvider(
        context: Context
    ): ResourceProvider = ResourceProviderCore(
        context = context
    )

    @Provides
    @Singleton
    fun provideProjectConfigurationInfo(): ProjectConfigurationInfo = ProjectConfigurationInfoCore()

    @Provides
    @Singleton
    fun analyticProvider(): AnalyticManager = AnalyticManager()

    @Provides
    @Singleton
    fun applicationInfo(): AppInfoInteractor = AppInfoInteractorImpl(
        appVersion = BuildConfig.VERSION_NAME,
        buildNumber = BuildConfig.VERSION_CODE.toString()
    )
}