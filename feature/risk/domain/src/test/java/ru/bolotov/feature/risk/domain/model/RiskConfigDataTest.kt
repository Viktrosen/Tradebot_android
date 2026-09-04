package ru.bolotov.feature.risk.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для RiskConfig и RiskConfigData.
 *
 * Проверяют:
 * - корректность создания конфигурации риска
 * - значения по умолчанию
 * - валидацию данных
 */
class RiskConfigDataTest {

    @Test
    fun `RiskConfigData must have all required fields`() {
        val config = RiskConfigData(
            positionSizePercent = 5.0,
            positionSizePercentDisplay = "5%",
            stopLossPercent = 2.0,
            stopLossPercentDisplay = "2%",
            takeProfitPercent = 4.0,
            takeProfitPercentDisplay = "4%",
            maxCapitalUsage = 50.0,
            maxCapitalUsagePercent = "50%",
            maxPositions = 5,
            shortTradingEnabled = false
        )

        assertEquals(5.0, config.positionSizePercent, 0.001)
        assertEquals("5%", config.positionSizePercentDisplay)
        assertEquals(2.0, config.stopLossPercent, 0.001)
        assertEquals("2%", config.stopLossPercentDisplay)
        assertEquals(4.0, config.takeProfitPercent, 0.001)
        assertEquals("4%", config.takeProfitPercentDisplay)
        assertEquals(50.0, config.maxCapitalUsage, 0.001)
        assertEquals("50%", config.maxCapitalUsagePercent)
        assertEquals(5, config.maxPositions)
        assertFalse(config.shortTradingEnabled)
    }

    @Test
    fun `RiskConfigData must support short trading enabled`() {
        val config = RiskConfigData(
            positionSizePercent = 3.0,
            stopLossPercent = 1.5,
            takeProfitPercent = 3.0,
            maxCapitalUsage = 30.0,
            maxPositions = 3,
            shortTradingEnabled = true
        )

        assertTrue(config.shortTradingEnabled)
    }

    @Test
    fun `RiskConfigData display strings must be non-empty when set`() {
        val config = RiskConfigData(
            positionSizePercent = 5.0,
            positionSizePercentDisplay = "5%",
            stopLossPercent = 2.0,
            stopLossPercentDisplay = "2%",
            takeProfitPercent = 4.0,
            takeProfitPercentDisplay = "4%",
            maxCapitalUsage = 50.0,
            maxCapitalUsagePercent = "50%",
            maxPositions = 5
        )

        assertTrue(config.positionSizePercentDisplay.isNotBlank())
        assertTrue(config.stopLossPercentDisplay.isNotBlank())
        assertTrue(config.takeProfitPercentDisplay.isNotBlank())
        assertTrue(config.maxCapitalUsagePercent.isNotBlank())
    }

    @Test
    fun `RiskConfigData must be equal when all fields match`() {
        val config1 = RiskConfigData(
            positionSizePercent = 5.0,
            stopLossPercent = 2.0,
            takeProfitPercent = 4.0,
            maxCapitalUsage = 50.0,
            maxPositions = 5
        )

        val config2 = RiskConfigData(
            positionSizePercent = 5.0,
            stopLossPercent = 2.0,
            takeProfitPercent = 4.0,
            maxCapitalUsage = 50.0,
            maxPositions = 5
        )

        assertEquals(config1, config2)
    }

    @Test
    fun `RiskConfigData must support copy with modified fields`() {
        val config = RiskConfigData(
            positionSizePercent = 5.0,
            stopLossPercent = 2.0,
            takeProfitPercent = 4.0,
            maxCapitalUsage = 50.0,
            maxPositions = 5
        )

        val updatedConfig = config.copy(
            positionSizePercent = 7.0,
            shortTradingEnabled = true
        )

        assertEquals(7.0, updatedConfig.positionSizePercent, 0.001)
        assertTrue(updatedConfig.shortTradingEnabled)
        assertEquals(2.0, updatedConfig.stopLossPercent, 0.001)
    }

    @Test
    fun `RiskConfigData must have valid hashCode`() {
        val config = RiskConfigData(
            positionSizePercent = 5.0,
            stopLossPercent = 2.0,
            takeProfitPercent = 4.0,
            maxCapitalUsage = 50.0,
            maxPositions = 5
        )

        val configCopy = config.copy()
        assertEquals(config.hashCode(), configCopy.hashCode())
    }

    @Test
    fun `RiskConfigData must support zero values`() {
        val config = RiskConfigData(
            positionSizePercent = 0.0,
            stopLossPercent = 0.0,
            takeProfitPercent = 0.0,
            maxCapitalUsage = 0.0,
            maxPositions = 0
        )

        assertEquals(0.0, config.positionSizePercent, 0.001)
        assertEquals(0, config.maxPositions)
    }
}
