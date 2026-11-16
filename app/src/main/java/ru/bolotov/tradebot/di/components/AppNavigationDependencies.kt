package ru.bolotov.tradebot.di.components

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.more.domain.interactor.ThemeInteractor
import kotlinx.serialization.json.Json

/** Only the app host's session and appearance contracts, not feature UI dependencies. */
interface AppNavigationDependencies : Dependencies {
    val authInteractor: AuthInteractor
    val json: Json
    val themeInteractor: ThemeInteractor
}
