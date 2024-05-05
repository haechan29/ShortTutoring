package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.AuthTokenApi
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

abstract class FakeTokenAuthenticator: TokenAuthenticator {
    abstract val api: AuthTokenApi

    abstract val tokenNotFoundFailure: TokenNotFound
    abstract val invalidTokenFailure: InvalidToken

    override suspend fun authToken(): NetworkResult<Any> {
        with (readToken()) {
            if (this == null) return tokenNotFoundFailure
            if (!this.isValid()) return invalidTokenFailure
        }

        return api.authToken()
    }

    protected abstract suspend fun readToken(): LoginToken?
}