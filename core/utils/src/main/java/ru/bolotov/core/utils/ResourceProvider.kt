package ru.bolotov.core.utils

interface ResourceProvider {
    fun getString(id: Int): String

    fun getStringWithArgument(id: Int, arg: String): String

    fun getPlurals(id: Int, count: Int, arg: String): String
}