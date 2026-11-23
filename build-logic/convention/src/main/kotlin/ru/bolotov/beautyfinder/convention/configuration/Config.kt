import org.gradle.api.JavaVersion

object Config {

    const val NAMESPACE_PREFIX = "ru.bolotov."
    const val APPLICATION_ID = "ru.bolotov.beautyfinder"

    const val FILE_NAME = "beautyfinder"

    const val MIN_SDK = 28
    const val COMPILE_SDK = 36
    const val TARGET_SDK = 36

    const val VERSION_CODE = 2

    const val JVM_TOOL_CHAIN = 11

    val jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    val kotlinVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
    val javaVersion = JavaVersion.VERSION_17

    val versionName: String
        get() {
            val major = "1"
            val minor = "1"
            val patch = "0"

            return "$major.$minor.$patch"
        }

    object Dev {
        const val BACKEND_URL = "\"https://tradebotbackend-viktrosen.msk1.amvera.tech/api/bot/\""
        const val APPLICATION_ID_SUFFIX = ".dev"
    }

    object Prod {
        const val BACKEND_URL = "\"https://tradebotbackend-viktrosen.msk1.amvera.tech/api/bot/\""
    }
}