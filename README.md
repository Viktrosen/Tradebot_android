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
язык и часовой пояс. `BearerInterceptor` добавляет access token, `BearerAuthenticator`
однократно обновляет пару при HTTP 401. Credential exchange использует отдельный клиент без этих перехватчиков.

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
Flow и повторяет подключение с ограниченной задержкой (1–30 секунд), в том числе
при закрытии сокета по истечении JWT. При выходе защищённый граф удаляется и поток отменяется. HTTP остаётся
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
Числа с избыточной точностью в объяснении AI форматируются только на уровне
Compose BottomSheet до двух знаков после запятой; исходное объяснение и данные
сети не меняются.

### Strategies

`StrategyInteractor` предоставляет:

| Метод | Назначение |
|---|---|
| `getStrategies()` | Получает доступные стратегии и независимые конфигурации. |
| `candlestick(timeframe, minConfidence)` | Сохраняет настройки свечной стратегии. |
| `voting(weights)` | Сохраняет веса стратегии голосования. |
| `confirmation(indicators)` | Сохраняет набор подтверждающих индикаторов. |

Клиент работает только с автоматическим выбором: активную стратегию для каждого
инструмента определяет бот. На экране нет ручного переключения, диалога простой
стратегии или глобальной отметки «Текущая». Карточки Voting, Confirmation и
Candlestick открывают только настройки параметров; одиночные и неизвестные
алгоритмы отображаются без кнопки настройки и без действия по нажатию.

`StrategyViewModel` предзагружает сохранённые параметры в `UiState`; видимость
диалога также принадлежит состоянию, а не дублируется локальными Compose-флагами.
После сохранения клиент перечитывает конфигурации. Это не меняет назначение
стратегий режимам рынка. Каталог и настройки загружаются двумя запросами без
дополнительного чтения `command/status`. Legacy-поля текущей стратегии по-прежнему
десериализуются, но не управляют заголовком или выбором в UI.

Диалог Voting поддерживает EMA, RSI, MACD, BB, SuperTrend и VWAP. Участники
включаются явно: сохранение не добавляет отсутствующие индикаторы и не теряет
настроенные SuperTrend/VWAP. Вес включённого участника — целое число от 1 до 10,
нужны минимум два участника; отключение удаляет ключ, а не отправляет нулевой вес.
Черновик хранится в `UiState.votingDraft`, изменения проходят через MVI Action.
`domain/model/VotingWeights` формирует единственный проверенный payload; interactor
повторно проверяет параметры перед запросом. Если сервер вернул неизвестный
индикатор, он сохраняется в черновике, но отправка блокируется с объяснением —
клиент не должен молча удалить его настройки.

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
`BACKEND_URL`; авторизация выполняется вводом почты/пароля через gateway.
Пароли и токены никогда не добавляются в README или Git.

## Авторизация

`feature:auth:{api,data,domain,presentation,router}` следует той же Clean MVI архитектуре.
`AuthEntry` использует обычный `ComposableFeatureEntry` и типизированный `AuthGraph.Root`.
`AppViewModel` один раз восстанавливает сессию через `AuthInteractor` и отдаёт
`navigationState: StateFlow<AppNavigationState>` с `SharingStarted.Eagerly`.
`AppNavigationHost` собирает его внутри `repeatOnLifecycle(STARTED)` и переключает
Auth/Main; MainActivity только размещает host под общей темой. Проверка сессии и временный сбой восстановления отображаются на Auth (progress/retry).
Выход в разделе «Ещё» удаляет сессию; host очищает предыдущий back stack вместе
с ViewModel вложенных экранов и подписками. Обновление токенов той же сессии не
перезапускает Main. При восстановлении процесса старый Main back stack не используется
до проверки сессии; поворот экрана сохраняет навигацию в рамках живого AppViewModel.

Gateway endpoints: POST `/api/auth/login`, `/api/auth/refresh`, `/api/auth/logout`.
Первичного владельца создаёт оператор на gateway, регистрации в клиенте нет.
Пароль живёт только в состоянии формы и очищается после попытки входа, не сохраняется.
Токены не передаются в domain/presentation: data хранит пару атомарно в
EncryptedSharedPreferences с Android Keystore; файл исключён из backup/device-transfer,
backup приложения отключён. Credential traffic не попадает в Chucker/HTTP body logs.
Проверка TLS/hostname не отключается в debug.

В debug оба OkHttp-клиента (API и авторизация) используют `HttpLoggingInterceptor`
уровня `BASIC`: метод, URL, HTTP-код и время запроса доступны в Logcat с тегом
`TradebotHttp`. Заголовки и тела не выводятся; секретные заголовки дополнительно
помечены для редактирования. В release логирование отключено (`NONE`). Не передавайте
credentials через URL/query и не включайте `BODY` для этих клиентов.

Refresh сериализован одним lock для HTTP и WebSocket: параллельные старые 401
не вызывают несколько ротаций. При старте сохранённая сессия проверяется через refresh.
Сетевой сбой/5xx сохраняет пару и предлагает повторить, подтверждённый отказ refresh
возвращает экран входа. После потери ответа уже выполненной ротации может потребоваться
повторный login (сервер защищён от replay). При недоставленном logout локальная сессия
всё равно удаляется; серверный отзыв не гарантирован без сети, access ограничен TTL.

FCM регистрируется после входа и связан с серверной сессией устройства.
Старый hardcoded X-API-Key и фиктивная настройка ключа удалены. Старые версии
клиента не совместимы с JWT-защитой gateway — обновлять согласованно.

Проверки: `./gradlew.bat :feature:auth:data:testDebugUnitTest :app:assembleDevDebug`.
Ручной прогон: чистый запуск → вход → HTTP/WS → рестарт → refresh → logout →
Back не возвращает портфель; без сети — retry, при истёкшем refresh — повторный вход.
Не использовать реальные торговые команды для проверки авторизации.

## Схема исходных файлов

Согласованное размещение Kotlin-типов и точный список перенесённых классов:
[docs/CODE_LAYOUT.md](docs/CODE_LAYOUT.md). Это организационный рефакторинг без изменения
HTTP/RabbitMQ-контрактов, торговли, авторизации и структуры БД.

## Дизайн-система

Пять основных разделов используют общий графитовый интерфейс TradeBot с лаймовым
акцентом и розовой меткой бренда. Положительный P&L выделяется отдельным цветом
прибыли, отрицательный — цветом убытка. Опасные операции используют коралловый.

Источник токенов — `core:uikit`: `color/ColorPalette` (сырые пигменты),
`color/TradebotColors` (семантические роли), `typography/TradebotTypography`
(Inter и общая Material 3 шкала), `dimensions/TradebotDimens`,
`shapes/ClippedCornerShape` и `shapes/TradebotShapes`. `TradebotTheme` связывает
эти роли с MaterialTheme, включая диалоги, поля, переключатели и PullToRefresh.
Унаследованные цветовые группы от другого приложения удалены. Обе палитры
применяются глобально: LIGHT / DARK / SYSTEM выбираются на «Ещё» и сохраняются
в Preferences DataStore (`appearance.preferences_pb`, ключ `theme_mode`).
По умолчанию используется SYSTEM; неизвестное сохранённое значение также даёт SYSTEM.
Репозиторий получает единственный экземпляр DataStore через DI; domain interactor
отдаёт Flow, который наблюдают MoreViewModel и корневой AppViewModel. UI enum
маппится в доменный AppThemeMode явно. При старте splash остаётся до первого
чтения настройки, затем MainActivity применяет палитру и цвета системных панелей.
SYSTEM реагирует на изменения системной темы. Переключение не пересоздаёт
навигацию/сессию; настройка устройства сохраняется и после выхода из аккаунта.
При ошибке записи старый выбор остаётся действующим, экран показывает snackbar.
Ошибка чтения диска даёт временный SYSTEM с повторным чтением и ограниченной
паузой до 30 секунд; повреждённый файл восстанавливается стандартным
ReplaceFileCorruptionHandler. Исключения отмены не превращаются в ошибки UI.

Общие `TradebotCard`, `TradebotHeader`, `TradebotSectionTitle`,
`TradebotBadge`, `TradebotButton`, `TradebotMetric`, `TradebotNavigationRow`
и `TradebotIcon` находятся в `core:uikit/components`. Общие иконки —
XML-vector ресурсы `core:uikit/res/drawable`, без скачивания логотипов акций.
Брендовый знак `ic_tradebot_mark` — единый источник изображения в заголовках и
launcher foreground: прямая лаймовая «Т» со скошенным правым углом и розовым
подчёркиванием. Foreground
добавляет только прозрачные поля для safe area адаптивной иконки; фон не меняется.
Растровые launcher-иконки пяти плотностей, круглые варианты и Play Store PNG
экспортируются из того же вектора скриптом `python tools/export_launcher_icons.py`
(нужен Pillow). Экспорт проверяет центрирование знака в каждом размере.
Карточки позиций используют название инструмента и явную сторону LONG/SHORT;
тикер, если его нет в контракте, не придумывается.

Редизайн не меняет MVI actions/effects, модульные зависимости, сетевые контракты,
проверки рисков и подтверждения опасных действий. PullToRefresh остаётся в своём
контейнере с прежними callbacks и стандартным позиционированием; скелетонов и новых
режимов загрузки не добавлено. Дополнительные настройки и пункты меню доступны
через прокрутку — макет не является основанием удалять функциональность.

Каждый основной Content имеет приватный `@Preview` с локальными примерами, без
ViewModel и сети. Тексты интерфейса и диалогов пяти разделов вынесены в ресурсы:
`values` — русский по умолчанию, `values-en` — английский. Числа, деньги и
проценты форматируются по локали в `core:uikit/format`. Текст, полученный от
бэкенда/AI, остаётся исходным и не подменяется клиентом.

`TradebotSlider` и `TradebotSwitch` задают только оформление стандартных Material
controls. `TradebotPullToRefreshBox` сохраняет стандартную геометрию и поведение
Material PullToRefresh, меняя только цвета индикатора. Демо-позиции не попадают
в обычную навигацию: они используются только приватными Preview-функциями.

Диалоги используют общие `TradebotDialog`, `TradebotConfirmationDialog` и
`TradebotDialogButton` из `core:uikit/components`: скошенная панель, Inter,
прокручиваемое тело и доступные кнопки в ширину. Закрытие позиций, экстренное
закрытие и сброс риска имеют опасный коралловый акцент; отмена — нейтральная.
Настройки стратегий, фильтров инструментов и сообщения результата используют
тот же каркас. Диалоги имеют приватные Preview; подтверждение, отмена и MVI
actions остаются прежними.

### Карточка позиции

`:feature:positionDetails:{api,data,domain,presentation,router}` следует обычной
цепочке EntryCore → FeatureComponent → Component → ViewModel → Screen → Content.
Единственный источник полного снимка — gateway
`GET command/positions/{positionId}/details`. Закрытие переиспользует прежний
PositionsRepository, без нового торгового API или вычисления защиты на клиенте.

Карточки Dashboard/Positions открывают typed Root(positionId). Внутренний экран
скрывает BottomNavigation; Назад возвращает к месту открытия. FCM OPEN/CLOSE
пересобирает вложенный стек в Dashboard → PositionDetails и ждёт авторизации,
если сессии нет. MainActivity принимает cold/warm Intent через AppLinkParser. Отложенный AppLink
хранится в SavedStateHandle AppViewModel и обрабатывается единым Auth/Main host;
потреблённый target не повторяется при повороте. Старые push без ID открывают обычный главный экран.

Открытое состояние показывает цену, изменение относительно входа, P&L, количество
бумаг (лоты × размер лота), дату/время входа, стратегию, nullable уровни защиты,
раскрываемое объяснение. Закрытое — результат, вход/выход, причину и сохранённые
объяснения, без текущей цены, активной защиты и кнопки закрытия. Пропуски данных
не заменяются нулями или предположениями. Тексты ru/en, формы/цвета/Inter —
из core:uikit; Content имеет приватные демонстрационные Preview всех состояний.

Общий STOMP Flow разделяет одно подключение между экранами. Цена применяется
только при совпадении positionId; POSITIONS_CHANGED и reconnect обновляют снимок.
Закрытую карточку нельзя вернуть в открытое состояние устаревшим ответом.
После неизвестного результата закрытия UI проверяет снимок без повторной отправки
команды; не утверждает закрытие до подтверждения и сохраняет ожидание в SavedState.
### Внешние переходы

Канонический адрес позиции: `tradebot://positions/{positionId}`. AppLinkParser
нормализует URI, фоновые FCM extras и старый формат уведомлений; неизвестные URI
и некорректные идентификаторы отбрасываются. В app/navigation/data хранятся
AppLink и PendingAppLink. Единственный AppViewModel объединяет авторизацию и
отложенный запрос из SavedStateHandle; новый вход не обходит проверку сессии.

Main получает только MainGraph.Root(initialPositionId, launchId), без FCM или
app-зависимостей. После внешнего перехода стек всегда Dashboard → PositionDetails;
повторное нажатие создаёт новый launchId, поворот не повторяет переход.
NotificationPendingIntentFactory использует TaskStackBuilder для Activity task;
Compose-стек строит Main. Автоматические фоновые notification+data FCM проходят
через extras MainActivity, а не через локальную фабрику. Публичная ссылка открывает
только просмотр; торговых команд, токенов и внутренних адресов в ней нет.

### Проверка unit-тестов

Команды запуска, устройство ViewModel-тестов, общий Orbit helper и границы
проверок описаны в [docs/TESTING.md](docs/TESTING.md).
История задачи аудита — в [docs/TEST_FOLLOWUPS.md](docs/TEST_FOLLOWUPS.md).
Сборка APK не подменяет полный прогон `test`.
