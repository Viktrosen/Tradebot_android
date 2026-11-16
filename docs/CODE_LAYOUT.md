# Android: схема размещения Kotlin-типов

## Обязательные правила

1. Самостоятельный тип хранится в одноимённом файле своего слоя. Не складывать все типы проекта в общий data-пакет.
2. Сервисы, контроллеры и ViewModel не содержат самостоятельные DTO рядом с реализацией.
3. Варианты sealed-иерархий остаются вложенными в неё; extensions модели могут находиться в её файле.
4. Маленькие private-типы, используемые только одной реализацией, могут оставаться деталями её файла. Не расширять видимость ради формального дробления.
5. Перенос не меняет логику, wire-контракты, сериализованные имена маршрутов, таблицы, поля, аннотации и транзакции.
6. DTO с секретами не превращаются автоматически в data class; существующая семантика toString сохраняется.
7. Никаких пустых пакетов/абстракций на будущее. Следовать принятой архитектуре и модульным границам.

## Пакеты

Сохраняются Gradle-модули `api / data / domain / presentation / router`.

- `api/data` — сетевые request/response DTO.
- `data/repository`, `data/local`, `data/local/data`, `data/network` — реализации, хранилище и его модели, OkHttp.
- `domain/model`, `domain/exception`, `domain/repository`, `domain/interactor`, `domain/core` — чистые модели, ошибки, контракты и реализации interactor.
- `presentation/core` — ViewModel и EntryCore; `presentation/ui` — Compose; `presentation/di` — Dagger.
- `presentation/core/UiState.kt` — **единый файл UiState + Action + Effect**, отдельно от ViewModel. Пустой Effect не добавляется.
- `router` — Entry и типизированный Graph, в отдельных файлах.
- `app/navigation/data` — модель состояния корневой навигации и относящиеся к ней extensions.

## Точное расположение перенесённых типов

Этот список фиксирует выполненный первый этап, а не обещает массовую миграцию всех исторических файлов.

| Тип | Полное имя после переноса |
|---|---|
| `LoginRequest` | `ru.bolotov.feature.auth.api.data.LoginRequest` |
| `RefreshRequest` | `ru.bolotov.feature.auth.api.data.RefreshRequest` |
| `TokenResponse` | `ru.bolotov.feature.auth.api.data.TokenResponse` |
| `StoredTokens` | `ru.bolotov.feature.auth.data.local.data.StoredTokens` |
| `SecureTokenStore` | `ru.bolotov.feature.auth.data.local.SecureTokenStore` |
| `TokenStore` | `ru.bolotov.feature.auth.data.local.TokenStore` |
| `BearerAuthenticator` | `ru.bolotov.feature.auth.data.network.BearerAuthenticator` |
| `BearerInterceptor` | `ru.bolotov.feature.auth.data.network.BearerInterceptor` |
| `AuthRepositoryImpl` | `ru.bolotov.feature.auth.data.repository.AuthRepositoryImpl` |
| `AuthInteractorImpl` | `ru.bolotov.feature.auth.domain.core.AuthInteractorImpl` |
| `AuthUnavailable` | `ru.bolotov.feature.auth.domain.exception.AuthUnavailable` |
| `LoginRejected` | `ru.bolotov.feature.auth.domain.exception.LoginRejected` |
| `AuthInteractor` | `ru.bolotov.feature.auth.domain.interactor.AuthInteractor` |
| `AuthSession` | `ru.bolotov.feature.auth.domain.model.AuthSession` |
| `AuthRepository` | `ru.bolotov.feature.auth.domain.repository.AuthRepository` |
| `Action` | `ru.bolotov.feature.auth.presentation.core.Action` |
| `AuthEntryCore` | `ru.bolotov.feature.auth.presentation.core.AuthEntryCore` |
| `AuthViewModel` | `ru.bolotov.feature.auth.presentation.core.AuthViewModel` |
| `UiState` | `ru.bolotov.feature.auth.presentation.core.UiState` |
| `AuthComponent` | `ru.bolotov.feature.auth.presentation.di.AuthComponent` |
| `AuthDependencies` | `ru.bolotov.feature.auth.presentation.di.AuthDependencies` |
| `AuthScreen` | `ru.bolotov.feature.auth.presentation.ui.AuthScreen` |
| `AuthGraph` | `ru.bolotov.feature.auth.router.AuthGraph` |
| `AppNavigationState` | `ru.bolotov.tradebot.navigation.data.AppNavigationState` |
| `requiresNavigation` | `ru.bolotov.tradebot.navigation.data.requiresNavigation` |
| `toAppNavigationState` | `ru.bolotov.tradebot.navigation.data.toAppNavigationState` |

## Границы этапа

Только организация файлов и ссылок. Выделение новых клиентов/сервисов, изменение ошибок,
JPA equals/hashCode, форматирование всего проекта и настройки BODY-логирования сюда не входят.

## Общая дизайн-система

- `core:uikit/color` — `ColorPalette`, `TradebotColors`, тёмная/светлая семантика.
- `core:uikit/typography` — `TradebotTypography`, единая шкала Inter.
- `core:uikit/dimensions` — `TradebotDimens`.
- `core:uikit/shapes` — `ClippedCornerShape` и `TradebotShapes`.
- `core:uikit/components` — общие панели, заголовки, кнопки, бейджи, иконки,
  метрики, строки навигации и состояние ошибки: по одному компоненту в файле.
- `core:uikit/res/drawable/ic_*_outline.xml` — общие векторные пиктограммы.
- `feature/*/presentation/ui` — композиция экранов и специфичные карточки;
  `DashboardContent.kt` отделён от подписок и эффектов `DashboardScreen.kt`,
  `MorePreferenceCards.kt` группирует только тесно связанные элементы настроек.
Модули, доменные модели, API и MVI-владение состоянием не изменяются.

Для пяти основных разделов нужен приватный `@Preview` в том же файле, где
расположен state-based Content. Превью оборачивается в `TradebotTheme`, не создаёт
ViewModel и не обращается к сети. Демо-позиции существуют только внутри превью.
UI-строки лежат в ресурсах владельца компонента: русский в `values`, английский
в `values-en`. Общий формат денег и процентов — `core:uikit/format`.

Общие диалоги: `core:uikit/components/TradebotDialog.kt` (каркас),
`TradebotConfirmationDialog.kt` (подтверждение) и `TradebotDialogButton.kt`
(действие). Feature-диалоги оставляют у себя только тексты, поля и callbacks;
не создают второй общий стиль и не переносят действия во ViewModel другого слоя.

## Настройка темы приложения

- `feature:more/domain/model/AppThemeMode`, `repository/ThemeRepository`,
  `interactor/ThemeInteractor`, `core/ThemeInteractorImpl` — чистый контракт.
- `feature:more/data/local/ThemeDataStore` — единственный delegate Preferences
  DataStore; `data/repository/ThemeRepositoryImpl` — чтение/запись и fallback.
- `feature:more/presentation/core/ThemeMode.kt` — UI enum и явные маппинги;
  состояние/действие остаются в `UiState.kt`, подписка — в MoreViewModel.
- `app/di/modules/ThemeModule` — DI хранилища/interactor;
  `AppNavigationDependencies` — только контракты корневого host.
- `app/navigation/AppViewModel` — наблюдение начальной настройки;
  `app/theme/ResolveDarkTheme.kt` — разрешение SYSTEM на UI-границе.
  MainActivity применяет тему ко всей навигации, не меняя её ключ.
## Position details

`feature/positionDetails` keeps api/data/domain/presentation/router boundaries. MVI UiState, Action and Effect share presentation/core/UiState.kt; components are in presentation/di, DTOs in api/data/response, mappers in their owning layer. App owns AppLinkParser/Encoder and NotificationPendingIntentFactory. AppViewModel stores a PendingAppLink in SavedStateHandle. Main's router exposes entry arguments, never a notification coordinator or shared launch flow.
- `app/navigation/deeplink` — AppLinkContract, AppLinkParser, AppLinkEncoder; `app/navigation/data` — AppLink and PendingAppLink. `app/notifications/NotificationPendingIntentFactory` creates Activity PendingIntents. AppViewModelFactory supplies the restored SavedStateHandle to Dagger.
- `app/navigation/AppNavigationHost` — корневой NavController, lifecycle-aware сбор navigation StateFlow, переключение Auth/Main и подтверждение внешнего перехода. `AppViewModel` производит eager StateFlow, но не выполняет UI-навигацию. MainActivity размещает host под общей темой.
