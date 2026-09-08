# Tradebot Android client

Android-клиент для управления `TradeBot` через `TradebotBackend`. Приложение
показывает состояние портфеля и бота, позволяет изменять конфигурации торговли
и рисков, управлять выбранными инструментами и получать push-уведомления.

## Архитектура

Проект построен по Clean Architecture и MVI, с разделением каждой функции на
независимые Gradle-модули:

```text
feature/<name>/api            Retrofit DTO и контракт HTTP
feature/<name>/data           Repository и DTO → domain mapping
feature/<name>/domain         Модели, Repository/Interactor interfaces и use case
feature/<name>/presentation   Compose UI, MVI state/actions/effects/ViewModel
feature/<name>/router         Навигационный entry-point функции
```

`app` собирает Dagger-граф, настройки сети и навигацию. `core:network`
содержит общий Retrofit/OkHttp-код и поток обновлений STOMP. Представление не
знает о Retrofit и DTO: оно вызывает только Interactor своего доменного модуля.

Поток одного пользовательского действия:

```text
Compose UI → Action → ViewModel intent → Interactor → Repository → API
                                                     ↓
Compose UI ← UiState / Effect ← ViewModel ← domain model ← DTO mapper
```

## Навигация

`MainScreenHost` размещает корневые разделы, а `NavigationBottomBar` отображает
основную навигацию. Feature entry-классы (`DashboardEntryCore`,
`PositionsEntryCore`, `StrategyEntryCore`, `RiskEntryCore`,
`InstrumentsEntryCore`, `MoreEntryCore`) создают Dagger-компонент конкретной
функции, получают ViewModel и регистрируют её граф через `FeatureEntry`.

`Instruments` — вспомогательный экран, поэтому нижняя навигация скрыта на нём;
на остальные корневые разделы пользователь приходит через BottomNavigation.

## Сеть и обновления в реальном времени

### `NetworkModule`

Создаёт JSON-конвертер, OkHttp-клиент и Retrofit. В `provideBackendUrl()`
используется `BuildConfig.BACKEND_URL`, а `HeadersInterceptor` добавляет
`X-API-Key`, язык и часовой пояс к каждому запросу gateway.

### `TradingStateInteractor` и `StompTradingStateInteractor`

`TradingStateInteractor.updates()` возвращает `Flow<TradingStateUpdate>`.
Реализация `StompTradingStateInteractor` подключается к
`/ws/realtime`, подписывается на `/topic/trading-state`, разбирает STOMP frames
и публикует типизированные обновления:

- `PositionPrice` — новая цена и плавающий P&L открытой позиции;
- `Portfolio` — доступные средства;
- `PositionsChanged` — требуется повторная HTTP-загрузка списка;
- `BotStatus` — запуск или остановка бота;
- `TradingAvailability` — состояние торгов по инструментам.

`StompTradingStateInteractor` создаёт одно WebSocket-подключение на коллектора
Flow и не выполняет собственный автоматический reconnect. Поэтому HTTP остаётся
источником полного снимка: Dashboard обновляется при открытии/возврате на экран,
а на экранах данных доступен `PullToRefreshBox`. WebSocket дополняет уже
загруженное состояние быстрыми изменениями цены, портфеля и статуса.

## Функциональные модули

### Dashboard

`DashboardInteractor` — граница домена Dashboard:

| Метод | Назначение |
|---|---|
| `getBotStatus()` | Получает состояние запуска бота, выбранные для сканирования инструменты и доступность торгов. |
| `getDashboard()` | Загружает метрики, свободные средства и первые открытые позиции. |
| `getPositions()` | Возвращает открытые позиции для обновления списка. |
| `startBot()` / `stopBot()` | Отправляют команды управления ботом. |

`BotRepositoryImpl` вызывает gateway, а `BotMapper` превращает DTO в domain
модели. `DashboardViewModel` сразу применяет WebSocket-обновления цены,
свободных средств и статуса; при `PositionsChanged` обновляет данные целиком.

`DashboardScreen` отображает P&L, свободные средства, состояние бота, риски и
не более пяти открытых позиций. Глобальная «выбранная стратегия» здесь не
показывается: при адаптивной торговле она выбирается для каждого инструмента.
У открытой позиции показывается `entryStrategyName` — стратегия, по которой
она была открыта и продолжает сопровождаться. Для старых позиций без этого
поля строка скрыта. Если TradeBot подтвердил защитные уровни, dashboard также
показывает `brokerStopLossPrice` и бот-управляемый `managedExitPrice`.

**НОВОЕ (2026-09):**
- **MarketRegimeChip** — отображает текущий режим рынка с цветовой индикацией и статусом торговли.
- **EXTREME_VOLATILE Warning** — красный баннер появляется при экстремальной волатильности, блокируя новые входы.

### Positions

| Класс / метод | Назначение |
|---|---|
| `PositionsInteractor.getPositions()` | Возвращает все открытые и закрытые позиции, отсортированные по времени. |
| `PositionsInteractor.closePosition(positionId)` | Запрашивает ручное закрытие конкретной позиции. |
| `PositionsViewModel.handleAction()` | Единственная точка входа MVI-действий: загрузка, refresh, фильтры, диалог закрытия и BottomSheet с объяснением. |
| `PositionCard` | Показывает цены, P&L, дату, сторону позиции, стратегию входа, подтверждённый брокерский SL, активную защиту прибыли и AI/причину закрытия. |

`PositionsViewModel` хранит данные диалогов в `UiState`: `closingPosition` и
`infoPosition`. Это исключает локальное состояние Compose, поэтому диалог
закрывается при успешной операции и корректно переживает рекомпозицию.

### Strategies

`StrategyInteractor` предоставляет:

| Метод | Назначение |
|---|---|
| `getStrategies()` | Получает доступные стратегии и независимые конфигурации. |
| `candlestick(timeframe, minConfidence)` | Сохраняет настройки свечной стратегии. |
| `voting(weights)` | Сохраняет веса стратегии голосования. |
| `confirmation(indicators)` | Сохраняет набор подтверждающих индикаторов. |
| `simple(name)` | Оставлен для совместимости с командой простой стратегии backend. |

`StrategyViewModel` предзагружает текущие значения в состояние диалогов и
после успешного сохранения повторно загружает конфигурации. В адаптивном режиме
настройка карточки не означает глобального выбора стратегии: бот подбирает её
для каждого инструмента по режиму рынка.

**НОВОЕ (2026-09):**
- **NEW Badge** — визуальная метка для новых стратегий (SuperTrend, VWAP) отображается на Strategy Screen. Сопоставление устойчиво к регистру и пробелам в имени, которое возвращает backend.
- **Volume-Weighted Info** — описание volume-weighted candlesticks добавлено в настройки Candlestick стратегии.

### Risk

`RiskInteractor.getRiskConfig()`, `updateRiskConfig(config)` и
`resetRiskConfig()` управляют единым профилем риска. `RiskViewModel` проверяет
значения до запроса, хранит состояние сохранения и через `Effect` показывает
успешный или ошибочный результат. Настройки включают долю капитала на позицию,
stop-loss, take-profit, максимальную загрузку, количество позиций и разрешение
коротких продаж.

### Instruments

`InstrumentsInteractor` отделяет API отбора бумаг от UI:

| Метод | Назначение |
|---|---|
| `getInstruments()` | Получает выбранные ботом инструменты. |
| `rescanInstruments()` | Запускает повторный отбор по текущим фильтрам. |
| `getFilters()` | Загружает фильтры сканера. |
| `updateFilters(filters)` | Сохраняет фильтры перед следующим ресканом. |

`InstrumentsViewModel` отображает фильтры, результаты рескана и доступность
торгов; ручное обновление экрана выполняется свайпом вниз. Карточка фильтров
остаётся доступной и при пустом результате рескана: пользователь может изменить
ограничения и повторить отбор, не возвращаясь на другой экран.

### More и аварийное закрытие

`EmergencyCloseInteractor.closeAllPositions()` вызывает gateway-команду
экстренного закрытия. `MoreViewModel` сначала переводит `UiState` в состояние
подтверждения, и только действие «Да, закрыть» вызывает interactor. Бот после
этой команды закрывает позиции и останавливается.

### Уведомления

`FcmTokenRegistrar.register(fcmToken)` получает FCM token устройства и через
`NotificationsInteractor.registerDevice(installationId, fcmToken)` сохраняет
его в TradebotBackend. `TradebotMessagingService.onNewToken()` запускает
повторную регистрацию при смене токена, а `onMessageReceived()` обрабатывает
полученный push на устройстве.

## Модели позиции и совместимость API

`OpenPositionResponse` и dashboard DTO используют nullable
`entryStrategyName`, `brokerStopLossPrice`, `managedExitPrice` и
`profitProtectionStage`. Эти поля приходят от TradeBot через TradebotBackend
без изменения gateway и затем передаются по цепочке mapper → domain model →
`UiPosition`. Nullable-формат сохраняет совместимость со старыми позициями и
старыми ответами бота. Клиент только отображает уровни: рассчитывать, менять
или исполнять защиту прибыли вправе исключительно TradeBot.

## Сборка

```powershell
.\gradlew.bat :app:assembleDevDebug
.\gradlew.bat :feature:dashboard:presentation:compileDebugKotlin
.\gradlew.bat :feature:positions:presentation:compileDebugKotlin
```

Для работы приложения должны быть доступны TradebotBackend, его WebSocket и
Firebase-конфигурация Android. Адрес gateway задаётся вариантами сборки через
`BACKEND_URL`; API-ключ следует передавать только через конфигурацию приложения
и никогда не добавлять в README или Git.
