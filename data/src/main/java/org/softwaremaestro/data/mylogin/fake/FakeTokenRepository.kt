package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AttemptResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

class FakeTokenRepository(
    private val tokenStorage: TokenStorage,
    private val accessTokenAuthenticator: TokenAuthenticator,
    private val refreshTokenAuthenticator: TokenAuthenticator,
): TokenRepository<String> {

    override suspend fun authAccessToken(): AttemptResult<String> {
        return accessTokenAuthenticator.authToken()
    }

    override suspend fun authRefreshToken(): AttemptResult<String> {
        return refreshTokenAuthenticator.authToken()
    }

    override suspend fun save(token: LoginToken) {
        if (token.isValid()) {
            tokenStorage.save(token)
        }
    }

    override suspend fun load(): LoginToken? {
        val token = tokenStorage.load() ?: return null

        return if (token.isValid()) token else null
    }
}