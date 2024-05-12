package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.data.mylogin.util.NetworkSyncQueue.Companion.sync
import org.softwaremaestro.data.mylogin.util.dtoOrNull
import org.softwaremaestro.data.mylogin.util.nullIfOk
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.Authentication
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.Result
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenInjector

abstract class FakeTokenInjector(
    private val authenticator: TokenAuthenticator,
    private val tokenRepository: TokenRepository<LoginAccessToken>
): TokenInjector {
    private val accessTokenNotFound: AccessTokenNotFound = AccessTokenNotFound

    final override suspend fun injectToken(request: Request): NetworkResult<EmptyResponseDto> {
        checkTokenOrFail()?.let { failure -> return failure }

        val dto = loadAccessToken().dtoOrNull() ?: return accessTokenNotFound

        return addTokenToRequestHeader(dto.accessToken)
    }

    private suspend fun checkTokenOrFail(): NetworkFailure? {
        val authFailure = authenticateToken().nullIfOk() ?: return null

        return issueToken(authFailure).nullIfOk()
    }

    private suspend fun authenticateToken(): Result<Authentication> {
        return authenticator.authToken()
    }

    private suspend fun issueToken(authFailure: Failure<Authentication>): NetworkResult<EmptyResponseDto> {
        return sync { issueTokenFromServer(authFailure) }
    }

    private suspend fun issueTokenFromServer(authFailure: Failure<Authentication>): NetworkResult<EmptyResponseDto> {
        delay(100)
        return when (authFailure) {
            is AccessTokenIsNotAuthenticated -> issueAccessToken()
            is RefreshTokenIsNotAuthenticated -> issueRefreshToken()
            else -> throw Exception()
        }
    }

    private suspend fun issueAccessToken(): NetworkResult<EmptyResponseDto> {
        TODO()
    }

    private suspend fun issueRefreshToken(): NetworkResult<EmptyResponseDto> {
        TODO()
    }

    private suspend fun loadAccessToken(): NetworkResult<LocalTokenResponseDto> {
        return tokenRepository.load()
    }

    private fun addTokenToRequestHeader(token: LoginAccessToken): NetworkResult<EmptyResponseDto> {
        TODO()
    }
}