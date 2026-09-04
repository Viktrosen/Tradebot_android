package ru.bolotov.beautyfinder.convention.configuration

sealed class FlavorDimension(val name: String) {

    object Backend: FlavorDimension("backend") {
        const val DEV = "dev"
        const val PRODUCTION = "production"
    }
}
