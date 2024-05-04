package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.AttemptResult
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

abstract class FakeTokenAuthenticator: TokenAuthenticator {
    abstract val tokenNotFoundFailure: TokenNotFound
    abstract val invalidTokenFailure: InvalidToken

    override suspend fun authToken(): AttemptResult<String> {
        with (readToken()) {
            if (this == null) return tokenNotFoundFailure
            if (!this.isValid()) return invalidTokenFailure
        }

        return Ok("")
    }

    protected abstract suspend fun readToken(): LoginToken?
}