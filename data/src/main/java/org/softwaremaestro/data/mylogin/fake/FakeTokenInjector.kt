package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.data.mylogin.util.NetworkSyncQueue.Companion.sync
import org.softwaremaestro.data.mylogin.util.dtoOrNull
import org.softwaremaestro.data.mylogin.util.nullIfSuccess
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.result.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.result.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.result.AuthFailure
import org.softwaremaestro.domain.mylogin.entity.result.AuthResult
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.result.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenInjector
import org.softwaremaestro.domain.mylogin.entity.TokenIssuer
import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto

abstract class FakeTokenInjector(
    private val authenticator: TokenAuthenticator,
    private val tokenRepository: TokenRepository<LoginAccessToken>,
    private val accessTokenIssuer: TokenIssuer<LoginAccessToken>,
    private val refreshTokenIssuer: TokenIssuer<LoginRefreshToken>,
): TokenInjector {
    private val accessTokenNotFound: AccessTokenNotFound = AccessTokenNotFound

    final override suspend fun injectToken(request: Request<RequestDto>): NetworkResult<EmptyResponseDto> {
        checkTokenOrFail()?.let { failure -> return failure }

        val dto = loadAccessToken().dtoOrNull() ?: return accessTokenNotFound

        return addTokenToRequestHeader(dto.accessToken)
    }

    private suspend fun checkTokenOrFail(): NetworkFailure? {
        val authFailure = authenticateToken().nullIfSuccess() ?: return null

        return issueToken(authFailure).nullIfSuccess()
    }

    private suspend fun authenticateToken(): AuthResult {
        return authenticator.authToken()
    }

    private suspend fun issueToken(authFailure: AuthFailure): NetworkResult<EmptyResponseDto> {
        return sync { issueTokenFromServer(authFailure) }
    }

    private suspend fun issueTokenFromServer(authFailure: AuthFailure): NetworkResult<EmptyResponseDto> {
        delay(100)
        return when (authFailure) {
            is AccessTokenIsNotAuthenticated -> issueAccessToken()
            is RefreshTokenIsNotAuthenticated -> issueRefreshToken()
        }
    }

    private suspend fun issueAccessToken(): NetworkResult<EmptyResponseDto> {
        return accessTokenIssuer.issueToken()
    }

    private suspend fun issueRefreshToken(): NetworkResult<EmptyResponseDto> {
        return refreshTokenIssuer.issueToken()
    }

    private suspend fun loadAccessToken(): NetworkResult<LocalTokenResponseDto> {
        return tokenRepository.load()
    }

    private fun addTokenToRequestHeader(token: LoginAccessToken): NetworkResult<EmptyResponseDto> {
        TODO()
    }
}