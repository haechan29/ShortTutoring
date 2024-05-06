package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AuthResult
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.TotalTokenAuthenticator

object FakeTotalTokenAuthenticator: TotalTokenAuthenticator {
    override suspend fun authToken(): AuthResult {
        if (FakeAccessTokenAuthenticator.authToken() is Ok) {
            return AccessTokenIsAuthenticated
        }

        return when (FakeRefreshTokenAuthenticator.authToken()) {
            is Ok -> AccessTokenIsNotAuthenticated
            is Failure -> RefreshTokenIsNotAuthenticated
        }
    }
}