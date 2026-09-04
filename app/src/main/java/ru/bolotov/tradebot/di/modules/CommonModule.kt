package ru.bolotov.tradebot.di.modules

import dagger.Module
import dagger.Provides
import ru.bolotov.core.utils.UserToken
import ru.bolotov.feature.dashboard.domain.core.DashboardInteractorImpl
import ru.bolotov.feature.dashboard.domain.interactor.DashboardInteractor
import ru.bolotov.feature.dashboard.domain.repository.BotRepository
import ru.bolotov.feature.instruments.domain.core.InstrumentsInteractorImpl
import ru.bolotov.feature.more.domain.core.EmergencyCloseInteractorImpl
import ru.bolotov.feature.more.domain.interactor.EmergencyCloseInteractor
import ru.bolotov.feature.more.domain.repository.EmergencyCloseRepository
import ru.bolotov.feature.risk.domain.core.RiskInteractorImpl
import ru.bolotov.feature.risk.domain.interactor.RiskInteractor
import ru.bolotov.feature.risk.domain.repository.RiskRepository
import ru.bolotov.feature.instruments.domain.interactor.InstrumentsInteractor
import ru.bolotov.feature.instruments.domain.repository.InstrumentsRepository
import ru.bolotov.feature.notifications.domain.core.NotificationsInteractorImpl
import ru.bolotov.feature.notifications.domain.interactor.NotificationsInteractor
import ru.bolotov.feature.notifications.domain.repository.NotificationsRepository
import ru.bolotov.feature.positions.domain.core.PositionsInteractorImpl
import ru.bolotov.feature.positions.domain.interactor.PositionsInteractor
import ru.bolotov.feature.positions.domain.repository.PositionsRepository
import ru.bolotov.feature.strategy.domain.core.StrategyInteractorImpl
import ru.bolotov.feature.strategy.domain.interactor.StrategyInteractor
import ru.bolotov.feature.strategy.domain.repository.StrategyRepository

@Module(includes = [DataModule::class])
class CommonModule {


    @Provides
    fun provideDashboardInteractor(
        repository: BotRepository,
    ): DashboardInteractor {
        return DashboardInteractorImpl(
            repository = repository
        )
    }

    @Provides
    fun providePositionsInteractor(repository: PositionsRepository): PositionsInteractor = PositionsInteractorImpl(repository)
    @Provides fun provideInstrumentsInteractor(repository: InstrumentsRepository): InstrumentsInteractor = InstrumentsInteractorImpl(repository)
    @Provides fun provideEmergencyCloseInteractor(repository: EmergencyCloseRepository): EmergencyCloseInteractor = EmergencyCloseInteractorImpl(repository)
    @Provides fun provideRiskInteractor(repository: RiskRepository): RiskInteractor = RiskInteractorImpl(repository)
    @Provides fun provideNotificationsInteractor(repository: NotificationsRepository): NotificationsInteractor = NotificationsInteractorImpl(repository)
    @Provides fun provideStrategyInteractor(repository: StrategyRepository): StrategyInteractor = StrategyInteractorImpl(repository)

    /*@Provides
    fun provideMapInteractor(
        repository: MapRepository,
    ): MapInteractor {
        return MapInteractorImpl(
            mapRepository = repository
        )
    }

    @Provides
    fun provideProfileInteractor(
        repository: ProfileRepository,
        userToken: UserToken,
        fcmTokenRepository: FcmTokenRepository
    ): ProfileInteractor {
        return ProfileInteractorImpl(
            profileRepository = repository,
            userToken = userToken,
            fcmTokenRepository = fcmTokenRepository
        )
    }*/
}
