package ru.bolotov.feature.auth.data.local

import ru.bolotov.feature.auth.data.local.data.StoredTokens

/** Data-layer storage boundary; credentials never cross into domain or UI. */
interface TokenStore {
    fun read(): StoredTokens?
    fun write(tokens: StoredTokens)
    fun clear()
}
