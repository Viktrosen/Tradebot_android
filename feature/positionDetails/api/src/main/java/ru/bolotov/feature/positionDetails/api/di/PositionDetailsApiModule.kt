package ru.bolotov.feature.positionDetails.api.di

import dagger.Module
import dagger.Provides
import ru.bolotov.core.network.NetworkApiProvider
import ru.bolotov.feature.positionDetails.api.PositionDetailsApi

@Module
class PositionDetailsApiModule {
    @Provides
    fun providePositionDetailsApi(provider: NetworkApiProvider): PositionDetailsApi =
        provider.provideApiService(PositionDetailsApi::class)
}
