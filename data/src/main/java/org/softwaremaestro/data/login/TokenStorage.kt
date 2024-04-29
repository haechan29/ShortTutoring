package org.softwaremaestro.data.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.domain.login.entity.TokenNotFoundException

interface TokenStorage {
    suspend fun save(token: LoginToken)
    suspend fun load(): LoginToken
    suspend fun clear()
}

object FakeTokenStorage: TokenStorage {
    private var savedToken: LoginToken? = null

    override suspend fun save(token: LoginToken) {
        withContext(Dispatchers.IO) {
            delay(1000)
            // TODO
            savedToken = token
        }
    }

    override suspend fun load(): LoginToken {
        return withContext(Dispatchers.IO) {
            delay(1000)
            if (savedToken == null) throw TokenNotFoundException

            savedToken!!
        }
    }

    override suspend fun clear() {
        savedToken = null
    }
}