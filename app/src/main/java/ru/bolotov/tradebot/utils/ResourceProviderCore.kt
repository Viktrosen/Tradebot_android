package ru.bolotov.tradebot.utils

import android.content.Context
import ru.bolotov.core.utils.ResourceProvider
import javax.inject.Inject

class ResourceProviderCore @Inject constructor(
    private val context: Context
) : ResourceProvider {
    override fun getString(id: Int): String = runCatching {
        context.getString(id)
    }.getOrDefault("")

    override fun getStringWithArgument(id: Int, arg: String): String = runCatching {
        context.getString(id, arg)
    }.getOrDefault("")

    override fun getPlurals(id: Int, count: Int, arg: String): String = runCatching {
        context.resources.getQuantityString(id, count, arg)
    }.getOrDefault("")
}