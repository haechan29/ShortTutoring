package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.TokenStorage
import org.softwaremaestro.domain.mylogin.entity.LoginToken

abstract class FakeTokenStorage<Token: LoginToken>: TokenStorage<Token> {
    private var savedToken: Token? = null

    override suspend fun save(token: Token) {
        withContext(Dispatchers.IO) {
            delay(1000)
            // TODO
            savedToken = token
        }
    }

    override suspend fun load(): Token? {
        return withContext(Dispatchers.IO) {
            delay(1000)
            savedToken
        }
    }

    override suspend fun clear() {
        savedToken = null
    }
}

object FakeAccessTokenStorage: FakeTokenStorage<LoginAccessToken>()
object FakeRefreshTokenStorage: FakeTokenStorage<LoginRefreshToken>()