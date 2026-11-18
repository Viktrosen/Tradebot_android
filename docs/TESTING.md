# Android unit tests

## Commands

Run from the Android repository with the configured JDK/toolchain:

```powershell
.\gradlew.bat test --continue
.\gradlew.bat :feature:strategy:presentation:testDebugUnitTest
.\gradlew.bat :app:assembleDevDebug
```

`test` includes library debug/release and application dev/prod variants. Repeated
executions of the same test in different variants are not additional scenarios.
Reports are in each module's `build/reports/tests/<task>/index.html` and machine
readable results in `build/test-results/<task>/TEST-*.xml`.

## ViewModel tests

Real ViewModels receive fake domain interactors and realtime flows. Tests invoke
`handleAction` (`handle` in Auth) and assert state, effects and recorded requests.
Mockito supplies external analytics objects; Robolectric is used where Android
runtime methods such as `Bundle` are actually invoked. Android stub defaults are
not enabled.

Orbit's test scheduler setup is centralized in the test-only JVM module
[`core:testing`](../core/testing/README.md). Do not place cross-module Kotlin test
helpers in the former `core:utils/src/testFixtures` source set: that artifact was
empty in this toolchain. Feature test dependencies use
`testImplementation(project(path = ":core:testing", configuration = "testSupportClasses"))`.
This exports classes and packages them through the library convention's cached
artifact transform; it does not rebuild mutable `core/testing/build/libs/testing.jar`.
The reason and Windows coverage workflow are described in the helper README.

| ViewModel | Scenarios |
| --- | ---: |
| Dashboard | 8 |
| Positions | 9 |
| Risk | 8 |
| More | 9 |
| Instruments | 6 |
| Auth | 7 |
| Strategy | 9 |
| PositionDetails | 8 |
| MainHost | 1 |
| Splash | 1 |
| App (link/session/theme state) | 4 |

These 70 scenarios include initialization, actions, state/effects, failures,
pending/duplicate submissions and realtime behavior where applicable. They are
not exhaustive branch coverage. `AppEffectsHandlerViewModel` currently has no
behavior; adding a constructor-only test would not provide coverage of a feature.

## Audit notes (18 November 2026)

- Sorting tests call the actual Dashboard interactor, not a copied algorithm.
- Template arithmetic and declaration-only tests were removed; meaningful model,
  mapper, repository, authentication and formatting tests remain.
- Debug HTTP logging is checked as BASIC, release as NONE. Do not change tests to
  allow logging credential-bearing bodies.
- Shared network library release artifacts must retain public realtime types;
  shrinking belongs to the final application.
- Regression tests reject duplicate close/save/emergency commands and emergency
  commands without confirmation. Risk reset and save are mutually exclusive.
- PositionDetails rejects a stale open snapshot after closure and still finishes
  refresh, without re-enabling close controls.

No device/Compose instrumentation, real backend, broker orders, FCM delivery or
production credentials are used in this audit. Unit tests cannot replace those
integration checks.

## Verified result (18 November 2026)

`test` and `:app:assembleDevDebug` passed: 220 distinct scenarios, 466 executions
across variants, zero failures/skips. The two convention-transform tests passed
separately. All 20 Orbit-consuming presentation test tasks ran with the installed
JetBrains coverage agent while the old mutable `testing.jar` remained mapped in
memory. Their classpaths contained the cached `test-support.jar`, never the old
project JAR, and produced nonempty `.ic` files under `build/diagnostics/coverage`.
The exact native Windows 1224 message was also reproduced on a disposable copy.

This was a CLI-agent regression, not an automated click of Android Studio's
coverage action. Sync Gradle in the IDE before rerunning coverage. Existing
Kotlin/deprecation/native-symbol warnings are not counted as test failures.

The unused default helper `jar` task is disabled because an aggregate `test` can
schedule it even with no helper test sources. Its explicit `--rerun`, the helper
NO-SOURCE test task and strategy coverage were verified together under mapping.
Root `clean` is a typed `Delete` task: configuring it must not execute
`Project.delete`. A marker survived `help --task clean` and the coverage run;
no clean was executed. Diagnostic script/marker sources were removed afterwards.
