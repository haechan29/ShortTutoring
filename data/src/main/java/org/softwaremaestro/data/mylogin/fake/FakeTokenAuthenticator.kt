package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.Authentication
import org.softwaremaestro.domain.mylogin.entity.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.Result
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator

abstract class FakeTokenAuthenticator(
    private val accessTokenRepository: TokenRepository<LoginAccessToken>,
    private val refreshTokenRepository: TokenRepository<LoginRefreshToken>
): TokenAuthenticator {
    override suspend fun authToken(): Result<Authentication> {
        if (loadAccessToken() is NetworkSuccess) {
            return AccessTokenIsAuthenticated
        }

        return when (loadRefreshToken()) {
            is NetworkSuccess -> AccessTokenIsNotAuthenticated
            is NetworkFailure -> RefreshTokenIsNotAuthenticated
        }
    }

    private suspend fun loadAccessToken(): NetworkResult<LocalTokenResponseDto> {
        return accessTokenRepository.load()
    }

    private suspend fun loadRefreshToken(): NetworkResult<LocalTokenResponseDto> {
        return refreshTokenRepository.load()
    }
}