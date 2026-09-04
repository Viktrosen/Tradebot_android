package ru.bolotov.feature.instruments.domain.interactor

import ru.bolotov.feature.instruments.domain.model.TradingInstrument
import ru.bolotov.feature.instruments.domain.model.InstrumentFilters

/** Доменная граница списка бумаг, выбранных ботом, и параметров их отбора. */
interface InstrumentsInteractor {
    /** Загружает текущий результат отбора инструментов. */
    suspend fun getInstruments(): List<TradingInstrument>
    /** Запускает новый отбор бумаг с сохранёнными фильтрами. */
    suspend fun rescanInstruments(): List<TradingInstrument>
    /** Возвращает фильтры, которые будут применены при следующем рескане. */
    suspend fun getFilters(): InstrumentFilters
    /** Сохраняет фильтры отбора без автоматического запуска рескана. */
    suspend fun updateFilters(filters: InstrumentFilters)
}
