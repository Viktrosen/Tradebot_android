# Shared JVM test support

`:core:testing` contains shared test helpers, not application code. Feature modules
depend on it only through
`testImplementation(project(path = ":core:testing", configuration = "testSupportClasses"))`.
It has no Android plugin, resources or production module dependencies.

`ru.bolotov.core.testing.controlOrbit` puts Orbit 10 intents on the calling
`TestScope` scheduler. Setting only `Dispatchers.Main` does not control Orbit's
default event-loop dispatcher.

In a ViewModel fixture:

1. Set Main to `StandardTestDispatcher(testScheduler)` before constructing the model.
2. Register the model in a `ViewModelStore` for cleanup.
3. Call `controlOrbit(model)` before actions and before collecting effects.
4. Invoke the real `handleAction`/`handle`, then `runCurrent()` or await a specific
   state/effect. Use `CompletableDeferred` in fakes to inspect pending requests.
5. Clear the store and reset Main in teardown. Long-lived collectors belong in
   `backgroundScope`.

The helper completes constructor intents and preserves their state and buffered
effects before switching to Orbit's test container. Constructor dependencies must
not remain suspended indefinitely. Initialization started outside Orbit (for
example, `viewModelScope.launch`) may require awaiting its ready state first.
It intentionally uses an Orbit internal test seam: keep the opt-in centralized
here and verify this module when upgrading Orbit.

The previous Android `core:utils` test-fixtures artifact did not compile/package
this Kotlin helper in this project's toolchain. A public function in an empty
artifact cannot be imported by consumers; the JVM module fixes the build boundary.

## Windows coverage and artifact ownership

Do not replace the explicit `testSupportClasses` configuration with the default
project dependency. The default publishes mutable `build/libs/testing.jar`; an IDE
coverage classloader can retain a mapped view and prevent the next overwrite on
Windows (`ERROR_USER_MAPPED_FILE`, 1224). Package/import names do not cause it.

`testSupportClasses` exports the Kotlin class directory with its compile-task
dependency and transitive API/runtime libraries. AGP compilation still needs a
JAR, so `TestSupportClassesToJarTransform` in the library convention plugin
materializes one in Gradle's content-addressed transform cache. New class content
gets a separate cache artifact, not an overwrite of a mapped project JAR. The
helper module currently contains only Kotlin classes/metadata, no resources.

The transform is scoped to the custom artifact type, not all project dependencies.
The default `jar` task is disabled: an aggregate `test` can schedule it even when
this support-only module has no own test sources. All consumers must use the
explicit class variant, never the legacy/default archive.
Its unit tests preserve class bytes/Kotlin metadata and deterministic ZIP entries:

```powershell
.\gradlew.bat :build-logic:convention:test --max-workers=1
```

After changing build scripts, sync Gradle in Android Studio. Finish an existing
coverage run before starting another build/clean; do not delete locked JARs, add
blind retries or kill all JVMs. This change removes the mutable test-support JAR
from the consumer pipeline, not every possible Windows file-lock scenario.
