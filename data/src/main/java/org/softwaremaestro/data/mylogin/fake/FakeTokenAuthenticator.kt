package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AuthResult
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator

abstract class FakeTokenAuthenticator(
    private val accessTokenRepository: TokenRepository<LoginAccessToken>,
    private val refreshTokenRepository: TokenRepository<LoginRefreshToken>
): TokenAuthenticator {
    override suspend fun authToken(): AuthResult {
        if (accessTokenRepository.load() is Ok) {
            return AccessTokenIsAuthenticated
        }

        return when (refreshTokenRepository.load()) {
            is Ok -> AccessTokenIsNotAuthenticated
            is Failure -> RefreshTokenIsNotAuthenticated
        }
    }
}