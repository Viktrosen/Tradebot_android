package ru.bolotov.feature.risk.domain.interactor

import ru.bolotov.feature.risk.domain.model.RiskConfig

/** Доменная граница чтения, сохранения и сброса единого риск-профиля бота. */
interface RiskInteractor {
    /** Загружает актуальный риск-профиль. */
    suspend fun getRiskConfig(): RiskConfig
    /** Валидирует и сохраняет новый риск-профиль на стороне бота. */
    suspend fun updateRiskConfig(config: RiskConfig): RiskConfig
    /** Возвращает риск-профиль к серверным значениям по умолчанию. */
    suspend fun resetRiskConfig(): RiskConfig
}
