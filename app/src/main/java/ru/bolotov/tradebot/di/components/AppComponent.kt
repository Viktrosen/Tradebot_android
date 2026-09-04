package ru.bolotov.tradebot.di.components

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.bolotov.tradebot.di.dependencies.FeatureDependenciesModule
import ru.bolotov.tradebot.di.modules.AppModule
import ru.bolotov.tradebot.di.modules.CommonModule
import ru.bolotov.tradebot.di.modules.DataModule
import ru.bolotov.tradebot.di.modules.NavigationModule
import ru.bolotov.tradebot.di.modules.NetworkModule
import ru.bolotov.tradebot.TradebotApplication
import javax.inject.Scope
import javax.inject.Singleton

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope

@Component(
    modules = [
        AppModule::class,
        FeatureDependenciesModule::class,
        NavigationModule::class,
        DataModule::class,
        NetworkModule::class,
        CommonModule::class,
    ]
)

@AppScope
@Singleton
interface AppComponent: FeaturesScreenDependencies {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
    fun inject(application: TradebotApplication)
}