package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

class FakeTokenRepository(
    private val tokenStorage: TokenStorage,
    private val validator: TokenValidator,
    private val api: Api
): TokenRepository {
    override suspend fun authAccessToken() {
        val token = readAccessToken()
        if (token == null) {
            authRefreshToken()
            return
        }

        if (!validator.isValid(token)) {
            authRefreshToken()
            return
        }

//        api.send(token)
    }

    private suspend fun readAccessToken(): LoginAccessToken? {
        delay(100)
        return null
    }

    override suspend fun authRefreshToken() {

    }

    override suspend fun save(token: LoginToken) {
        val isValid = validator.isValid(token)

        if (isValid) {
            tokenStorage.save(token)
        }
    }

    override suspend fun load(): LoginToken? {
        val token = tokenStorage.load() ?: return null

        val isValid = validator.isValid(token)

        return if (isValid) token else null
    }
}