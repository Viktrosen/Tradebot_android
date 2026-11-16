# Tradebot Android client — instructions for contributors

## Purpose and system boundary

This is the Android application for monitoring and controlling the trading system.
It communicates only with `TradebotBackend` through HTTP, STOMP/WebSocket and
FCM. It must not call `TradeBot` directly and must not duplicate trade decisions,
risk checks or broker behavior.

The README is the source of truth for the modular architecture, navigation,
networking, realtime updates, supported screens and build variants. Read its
relevant section before changing a feature or a gateway contract.

## Module and layering rules

Each feature uses this directory layout:

```text
api / data / domain / presentation / router
```

- `api` holds Retrofit DTOs and HTTP contracts.
- `data` implements repositories and maps DTOs to domain models.
- `domain` holds models, repository/interactor contracts and use cases. It must
  not depend on Retrofit, Android UI or Compose.
- `presentation` contains Compose screens and MVI ViewModels/state/actions/effects.
- `router` provides a feature entry point and navigation integration. In the
  existing modules, presentation depends on domain and router, while data depends
  on domain and api; preserve that pattern rather than creating reverse links.
- `app` composes Dagger, root navigation, build configuration and app-wide Android
  integrations.
- `core:network`/`service:network` own shared HTTP, STOMP and serialization code.

Do not bypass an interactor from a Composable or ViewModel by calling Retrofit or
a data repository directly.

## Cross-project contracts

- Consume only `TradebotBackend` public HTTP endpoints and `/ws/realtime` topics.
  Backend routes and DTOs may reflect TradeBot state but are not owned here.
- Treat `entryStrategyName` and newly added backend fields as backward-compatible:
  nullable fields and unknown enum/string values must not crash an older app.
- `brokerStopLossPrice`, `managedExitPrice` and `profitProtectionStage` are
  executor-owned, nullable display fields. Map and render them when present, but
  do not calculate risk levels, advance a protection stage or initiate an exit
  from the Android client.
- Market regimes are currently private TradeBot logic and are intentionally not a
  mobile contract. Do not add UI models, mappers or screens for them until a
  versioned gateway contract is explicitly introduced.
- HTTP remains the authoritative complete snapshot. STOMP is incremental UI
  enrichment; a lost WebSocket update must be recoverable through refresh.
- FCM token registration uses the authenticated gateway session and must never expose tokens
  or Firebase configuration in source control.

## UI and state rules

- Keep durable screen state in `UiState`; emit one-off navigation, snackbar and
  confirmation events through `Effect`.
- Presentation-only text formatting (for example, readable numeric precision in
  an AI explanation) belongs in the Compose layer and must not alter a DTO,
  domain model or the source explanation persisted by TradeBot.
- Make ViewModel actions the single entry point for user intent. Avoid local
  Compose state for business state that must survive recomposition.
- Reuse the existing design system and feature conventions before adding another
  screen-specific abstraction.
- Strategy cards display backend-provided catalog data. The app may configure only
  settings exposed by the gateway; it does not select the live strategy per
  instrument when adaptive mode is active.

## Bug investigation standard

Treat a bug report as evidence of a potentially broken invariant, not merely as a
case to suppress. Reconstruct the full path through Compose state, ViewModel MVI
actions/effects, interactor, repository, HTTP/STOMP/FCM contract and refresh
behavior. Look for the architectural cause: invalid state ownership, lifecycle or
recomposition issue, stale snapshot, lost incremental update, mapper/contract
mismatch, or a missing error transition.

Fix the defect in the layer that owns the invariant and add a regression test.
Do not mask it with screen-specific local state, an arbitrary delay, swallowed
error, redundant network call or a UI-only special case. If a short-term
mitigation is needed for user safety, keep it explicit and document the
root-cause follow-up.

## Secrets and generated files

- `feature:auth` owns the session flow through the usual api/data/domain/presentation/router
  boundaries. Domain/UI never receive access or refresh tokens.
- `AuthEntry` is an ordinary `ComposableFeatureEntry` with a typed route, like other screens.
  `AppViewModel` restores/observes the session through `AuthInteractor`; MainActivity
  collects navigation state with lifecycle awareness and replaces Auth/Main, clearing
  the previous back stack and its ViewModels. Token refresh within a session must
  not reset navigation. Never restore a protected graph before checking a session.
- Rotate tokens under one shared data-layer lock, store the entire pair atomically
  in encrypted preferences, exclude it from backups, and never log credential payloads.
- HTTP logging is debug-only BASIC metadata (`TradebotHttp`); release uses NONE.
  Never enable BODY on the shared API/auth clients or put credentials in URLs.
- Temporary network/5xx failures retain the encrypted session for retry. Definitive
  refresh rejection clears it. The auth HTTP client must not invoke its own authenticator.
- WebSocket uses Bearer handshake and reconnects after expiry; FCM registration is
  bound to the signed-in session. Never embed API keys or disable hostname verification.

- Never commit API keys, `local.properties`, signing material, `google-services.json`
  or generated build artifacts. Use the existing `.gitignore` conventions.
- Do not log authorization headers, FCM tokens or full backend error bodies.
- Preserve existing local build configuration and signing files.

## Verification

Prefer the narrowest affected module, then an application build or complete test
suite when changing shared contracts:

```powershell
.\gradlew.bat test
.\gradlew.bat :app:assembleDevDebug
.\gradlew.bat :feature:dashboard:presentation:compileDebugKotlin
.\gradlew.bat :feature:positions:presentation:compileDebugKotlin
```

Use an emulator/device check for changes to navigation, Compose layout, FCM or
WebSocket behavior; compilation alone does not validate those flows.

## Working-tree discipline

Preserve unrelated local Android Studio files and generated outputs. Do not create
commits unless the user explicitly asks for one.

## Kotlin source layout

Follow [docs/CODE_LAYOUT.md](docs/CODE_LAYOUT.md) for exact package placement.
Keep independent DTOs, entities, events, exceptions and interfaces in separate named files
at their owning layer. Do not mix unrelated types into service/controller/ViewModel files.
Android MVI is an explicit exception: keep UiState, Action and Effect together in
presentation/core/UiState.kt, separate from the ViewModel. Preserve nested sealed variants,
private implementation details, visibility, contracts and behaviour during moves.

## Shared visual system

Use the semantic roles from `TradebotTheme.colors` and MaterialTheme, not ad-hoc
hex colors in feature screens. The raw `color/ColorPalette` is internal to UI-kit.
Typography, spacing and shapes belong to `core:uikit/{typography,dimensions,shapes}`;
shared components belong to `core:uikit/components`. Keep the clipped-corner shape
in its own file. Shared outline icons are XML vectors in UI-kit resources.
The brand mark has one source: `ic_tradebot_mark.xml`. Adaptive foreground refers
to it; regenerate legacy WebP sizes and the store PNG with
`python tools/export_launcher_icons.py` (Pillow), never position separate copies
of the mark manually. Keep the exporter's per-size centering checks passing.
Do not introduce remote equity logos, made-up tickers or dummy portfolio data.
Profit/loss, LONG/SHORT and dangerous actions have distinct semantic colors.
Refresh styling must not change PullToRefresh placement, state or callbacks.
Do not add skeleton/loading modes as part of this redesign. Do not remove existing
menu actions, confirmation dialogs or risk settings to fit a static mockup.

Every main screen Content must have a private @Preview in the same file, using
TradebotTheme and preview-only data without ViewModel/network access. Keep UI
strings in the owning feature resources, or UI-kit resources for shared components.
Russian is the default values catalog; provide matching English values-en strings.
Never hardcode translated labels or embed localized prose into domain models.

Use TradebotDialog/TradebotConfirmationDialog and TradebotDialogButton from UI-kit
for dialogs. Keep body content scrollable and actions reachable on small screens;
destructive confirmations use the danger variant, with a neutral cancellation.
Feature resources own titles and messages (ru/en). Preserve dismissal/confirmation
callbacks and add private, network-free dialog previews.

## Appearance preference

Theme belongs to feature:more domain/data, not UI-kit or auth. Keep AppThemeMode,
ThemeRepository and ThemeInteractor domain-only; inject the single Preferences
DataStore into ThemeRepositoryImpl. UI ThemeMode maps explicitly at presentation.
Both MoreViewModel and AppViewModel observe the persisted flow: refresh must not
reset it and failed writes must not show an unsaved selection. MainActivity
resolves SYSTEM against system configuration, collects with lifecycle awareness
and applies one root TradebotTheme plus matching system bar icons. Keep the splash
until the initial preference is ready; never key navigation by the palette.
## Position-details contract

Details are keyed by positionId, never the instrument's latest trade. The executor
owns complete historical and active state; the gateway proxies it unchanged.
Quantity is lots with explicit lotSize. Missing evidence remains nullable.
Closed/reconciled lifecycles must not expose active close controls or fabricated
execution price/P&L. Preserve the distinction between broker SL, bot TP and managed
profit protection. Notification OPEN/CLOSE targets use the same positionId.
Android notifications rebuild Dashboard → Details after authentication; ordinary
in-app navigation keeps its origin. Do not verify this feature by sending real orders.
External navigation belongs to app/navigation/deeplink and AppViewModel. Normalize
URI/FCM extras into AppLink once at the Android boundary; persist a pending request
in the Activity-owned SavedStateHandle, not an application singleton or feature
router flow. Integrate authorization and pending links in the same root navigation
collector. Main accepts typed entry arguments only and owns Dashboard → Details.
TaskStackBuilder manages Activity tasks, not Compose destinations. Keep foreground,
background, legacy notification, repeat-tap and rotation behavior compatible.
