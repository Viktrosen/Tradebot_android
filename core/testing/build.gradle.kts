import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

kotlin.jvmToolchain(17)

dependencies {
    api(libs.jetbrains.coroutines.test)
    api(libs.orbit.core)
}

// Aggregate `test` can schedule this even when this support-only module has no
// own tests. Consumers use testSupportClasses, so never rewrite the legacy JAR.
tasks.named<Jar>("jar") {
    enabled = false
}
// Android's unit-test artifact views can still select the main JAR despite a
// classes preference on the dependency. Export an explicit directory-only
// variant so coverage cannot make a subsequent test rebuild overwrite that JAR.
configurations.create("testSupportClasses") {
    isCanBeResolved = false
    isCanBeConsumed = true
    extendsFrom(configurations.implementation.get(), configurations.runtimeOnly.get())
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.CLASSES))
    }
    outgoing.artifact(tasks.named<KotlinCompile>("compileKotlin").flatMap { it.destinationDirectory }) {
        type = "tradebot-test-support-classes"
        builtBy(tasks.named("compileKotlin"))
    }
}
