package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidAccessToken
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

class FakeAccessTokenAuthenticator: FakeTokenAuthenticator() {
    override val tokenNotFoundFailure: TokenNotFound
        get() = AccessTokenNotFound

    override val invalidTokenFailure: InvalidToken
        get() = InvalidAccessToken

    override suspend fun readToken(): LoginToken? {
        delay(100)
        return null
    }
}