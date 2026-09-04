package ru.bolotov.tradebot.di.modules

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.FeatureEntryKey
import ru.bolotov.feature.dashboard.presentation.core.DashboardEntryCore
import ru.bolotov.feature.dashboard.router.DashboardEntry
import ru.bolotov.feature.instruments.presentation.core.InstrumentsEntryCore
import ru.bolotov.feature.instruments.router.InstrumentsEntry
import ru.bolotov.feature.main.presentation.core.MainEntryCore
import ru.bolotov.feature.main.route.MainEntry
import ru.bolotov.feature.more.router.MoreEntry
import ru.bolotov.feature.positions.presentation.core.PositionsEntryCore
import ru.bolotov.feature.positions.router.PositionsEntry
import ru.bolotov.feature.risk.presentation.core.RiskEntryCore
import ru.bolotov.feature.risk.router.RiskEntry
import ru.bolotov.feature.splashScreen.presentation.core.SplashScreenEntryCore
import ru.bolotov.feature.splashScreen.router.SplashScreenEntry
import ru.bolotov.feature.strategy.presentation.core.StrategyEntryCore
import ru.bolotov.feature.strategy.router.StrategyEntry
import ru.bolotov.features.more.presentation.core.MoreEntryCore
import ru.bolotov.tradebot.router.FeatureEntry

@Module
interface NavigationModule {


    @Binds
    @IntoMap
    @FeatureEntryKey(SplashScreenEntry::class)
    fun bindSplashScreenEntry(entry: SplashScreenEntryCore): FeatureEntry

    @Binds
    @IntoMap
    @FeatureEntryKey(MainEntry::class)
    fun bindMainEntry(entry: MainEntryCore): FeatureEntry

    @Binds
    @IntoMap
    @FeatureEntryKey(DashboardEntry::class)
    fun bindDashboardScreenEntry(entry: DashboardEntryCore): FeatureEntry

    @Binds @IntoMap @FeatureEntryKey(InstrumentsEntry::class)
    fun bindInstrumentsScreenEntry(entry: InstrumentsEntryCore): FeatureEntry

    @Binds
    @IntoMap
    @FeatureEntryKey(PositionsEntry::class)
    fun bindPositionsScreenEntry(entry: PositionsEntryCore): FeatureEntry

    @Binds
    @IntoMap
    @FeatureEntryKey(RiskEntry::class)
    fun bindRiskScreenEntry(entry: RiskEntryCore): FeatureEntry

    @Binds
    @IntoMap
    @FeatureEntryKey(StrategyEntry::class)
    fun bindStrategyScreenEntry(entry: StrategyEntryCore): FeatureEntry

    @Binds
    @IntoMap
    @FeatureEntryKey(MoreEntry::class)
    fun bindMoreScreenEntry(entry: MoreEntryCore): FeatureEntry


}
