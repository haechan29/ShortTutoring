package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.domain.mylogin.entity.AuthTokenApi
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidRefreshToken
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

object FakeRefreshTokenAuthenticator: FakeTokenAuthenticator() {
    override val api: AuthTokenApi = FakeAuthRefreshTokenApi

    override val tokenNotFoundFailure: TokenNotFound = RefreshTokenNotFound
    override val invalidTokenFailure: InvalidToken = InvalidRefreshToken

    override suspend fun readToken(): LoginToken? {
        delay(100)
        return null
    }
}