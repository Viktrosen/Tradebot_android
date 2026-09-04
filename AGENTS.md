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
- Market regimes are currently private TradeBot logic and are intentionally not a
  mobile contract. Do not add UI models, mappers or screens for them until a
  versioned gateway contract is explicitly introduced.
- HTTP remains the authoritative complete snapshot. STOMP is incremental UI
  enrichment; a lost WebSocket update must be recoverable through refresh.
- FCM token registration uses the gateway and must never expose the client API key
  or Firebase configuration in source control.

## UI and state rules

- Keep durable screen state in `UiState`; emit one-off navigation, snackbar and
  confirmation events through `Effect`.
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
