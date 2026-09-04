package ru.bolotov.tradebot.di.modules

import dagger.Binds
import dagger.Module
import ru.bolotov.feature.dashboard.data.BotRepositoryImpl
import ru.bolotov.feature.dashboard.domain.repository.BotRepository
import ru.bolotov.feature.instruments.data.InstrumentsRepositoryImpl
import ru.bolotov.feature.more.data.EmergencyCloseRepositoryImpl
import ru.bolotov.feature.more.domain.repository.EmergencyCloseRepository
import ru.bolotov.feature.risk.data.RiskRepositoryImpl
import ru.bolotov.feature.risk.domain.repository.RiskRepository
import ru.bolotov.feature.instruments.domain.repository.InstrumentsRepository
import ru.bolotov.feature.notifications.data.NotificationsRepositoryImpl
import ru.bolotov.feature.notifications.domain.repository.NotificationsRepository
import ru.bolotov.feature.positions.data.PositionsRepositoryImpl
import ru.bolotov.feature.positions.domain.repository.PositionsRepository
import ru.bolotov.feature.strategy.data.StrategyRepositoryImpl
import ru.bolotov.feature.strategy.domain.repository.StrategyRepository

@Module(includes = [NetworkModule::class])
interface DataModule {

    @Binds
    fun bindsBotRepository(impl: BotRepositoryImpl): BotRepository
    @Binds fun bindsInstrumentsRepository(impl: InstrumentsRepositoryImpl): InstrumentsRepository
    @Binds fun bindsNotificationsRepository(impl: NotificationsRepositoryImpl): NotificationsRepository

    @Binds
    fun bindsPositionsRepository(impl: PositionsRepositoryImpl): PositionsRepository
    @Binds fun bindsStrategyRepository(impl: StrategyRepositoryImpl): StrategyRepository
    @Binds fun bindsEmergencyCloseRepository(impl: EmergencyCloseRepositoryImpl): EmergencyCloseRepository
    @Binds fun bindsRiskRepository(impl: RiskRepositoryImpl): RiskRepository

}
