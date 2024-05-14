package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.result.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.result.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.result.AuthResult
import org.softwaremaestro.domain.mylogin.entity.dto.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.result.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.result.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator

abstract class FakeTokenAuthenticator(
    private val accessTokenRepository: TokenRepository<LoginAccessToken>,
    private val refreshTokenRepository: TokenRepository<LoginRefreshToken>
): TokenAuthenticator {
    override suspend fun authToken(): AuthResult {
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