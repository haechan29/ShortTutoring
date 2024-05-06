package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AuthResult
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator

object FakeTokenAuthenticator: TokenAuthenticator {
    override suspend fun authToken(): AuthResult {
        if (FakeAccessTokenRepository.load() is Ok) {
            return AccessTokenIsAuthenticated
        }

        return when (FakeRefreshTokenRepository.load()) {
            is Ok -> AccessTokenIsNotAuthenticated
            is Failure -> RefreshTokenIsNotAuthenticated
        }
    }
}