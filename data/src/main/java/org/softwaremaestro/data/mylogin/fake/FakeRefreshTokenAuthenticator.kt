package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidRefreshToken
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

class FakeRefreshTokenAuthenticator: FakeTokenAuthenticator() {
    override val tokenNotFoundFailure: TokenNotFound
        get() = RefreshTokenNotFound

    override val invalidTokenFailure: InvalidToken
        get() = InvalidRefreshToken

    override suspend fun readToken(): LoginToken? {
        delay(100)
        return null
    }
}