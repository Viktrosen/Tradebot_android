package ru.bolotov.core.network

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

/**
 * STOMP-реализация [TradingStateInteractor].
 *
 * Подключается к gateway, подписывается на `/topic/trading-state` после STOMP
 * handshake и преобразует только поддерживаемые события в domain-neutral state.
 */
class StompTradingStateInteractor @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json,
    private val webSocketUrl: String
) : TradingStateInteractor {

    /** Создаёт одно WebSocket-подключение на коллектора Flow и закрывает его при отмене. */
    override fun updates(): Flow<TradingStateUpdate> = callbackFlow {
        val socket = client.newWebSocket(
            Request.Builder().url(webSocketUrl).build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    webSocket.send("CONNECT\naccept-version:1.2\n\n\u0000")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    if (text.startsWith("CONNECTED")) {
                        subscribeToTradingState(webSocket)
                        return
                    }

                    parseUpdate(text)?.let { update ->
                        logPriceUpdate(update)
                        trySend(update)
                    }
                }
            }
        )
        awaitClose { socket.close(1000, null) }
    }

    /** Разбирает STOMP frame; неизвестные или неполные события безопасно отбрасываются. */
    private fun parseUpdate(frame: String): TradingStateUpdate? {
        val body = frame.substringAfter("\n\n", "").removeSuffix("\u0000")
        if (body.isBlank()) return null
        val event = json.parseToJsonElement(body).jsonObject
        val type = event["eventType"]?.jsonPrimitive?.content ?: return null
        val data = event["data"]?.jsonObject ?: JsonObject(emptyMap())
        return when (type) {
            "POSITION_PRICE_UPDATED" -> TradingStateUpdate.PositionPrice(
                instrumentId = data.string("instrumentId") ?: return null,
                currentPrice = data.double("currentPrice") ?: return null,
                unrealizedPnl = data.double("unrealizedPnl") ?: return null,
                pnlPercent = data.double("pnlPercent") ?: return null
            )
            "PORTFOLIO_CHANGED" -> TradingStateUpdate.Portfolio(
                totalValue = data.double("totalValue"),
                availableCash = data.double("availableCash"),
                blockedCash = data.double("blockedCash")
            )
            "POSITIONS_CHANGED" -> TradingStateUpdate.PositionsChanged
            "BOT_STATUS_CHANGED" -> TradingStateUpdate.BotStatus(data.string("status") == "RUNNING")
            "TRADING_AVAILABILITY_CHANGED" -> TradingStateUpdate.TradingAvailability(
                instruments = data["instruments"]
                    ?.jsonArray
                    ?.mapNotNull { instrument ->
                        val item = instrument.jsonObject
                        val instrumentId = item.string("instrumentId") ?: return@mapNotNull null
                        val tradingAvailable = item.string("tradingAvailable")
                            ?.toBooleanStrictOrNull()
                            ?: return@mapNotNull null
                        instrumentId to tradingAvailable
                    }
                    ?.toMap()
                    ?: return null,
                allTradingUnavailable = data.string("allTradingUnavailable")
                    ?.toBooleanStrictOrNull()
                    ?: return null
            )
            else -> null
        }
    }

    private fun subscribeToTradingState(webSocket: WebSocket) {
        webSocket.send("SUBSCRIBE\nid:trading-state\ndestination:/topic/trading-state\n\n\u0000")
        Log.d(LOG_TAG, "Subscribed to /topic/trading-state")
    }

    private fun logPriceUpdate(update: TradingStateUpdate) {
        if (update is TradingStateUpdate.PositionPrice) {
            Log.d(LOG_TAG, "Price update received: ${update.instrumentId}")
        }
    }

    private fun JsonObject.string(name: String): String? = this[name]?.jsonPrimitive?.content
    private fun JsonObject.double(name: String): Double? =
        this[name]?.jsonPrimitive?.content?.toDoubleOrNull()

    private companion object {
        const val LOG_TAG = "TradingStateSocket"
    }
}
