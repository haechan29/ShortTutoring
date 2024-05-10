package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.data.mylogin.SyncQueue
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.AuthFailure
import org.softwaremaestro.domain.mylogin.entity.AuthOk
import org.softwaremaestro.domain.mylogin.entity.AuthResult
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.NetworkOk
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenInjector

abstract class FakeTokenInjector(
    private val authenticator: TokenAuthenticator,
    private val syncQueue: SyncQueue<NetworkResult<EmptyResponseDto>>,
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

    private suspend fun authenticateToken(): AuthResult {
        return authenticator.authToken()
    }

    private suspend fun issueToken(authFailure: AuthFailure): NetworkResult<EmptyResponseDto> {
        return syncQueue.sync { issueTokenFromServer(authFailure) }
    }

    private suspend fun issueTokenFromServer(authFailure: AuthFailure): NetworkResult<EmptyResponseDto> {
        delay(100)
        return when (authFailure) {
            is AccessTokenIsNotAuthenticated -> issueAccessToken()
            is RefreshTokenIsNotAuthenticated -> issueRefreshToken()
        }
    }

    private fun AuthResult.nullIfOk(): AuthFailure? {
        return when (this) {
            is AuthOk -> null
            is AuthFailure -> this
        }
    }

    private fun NetworkResult<EmptyResponseDto>.nullIfOk(): NetworkFailure? {
        return when (this) {
            is NetworkOk<EmptyResponseDto> -> null
            is NetworkFailure -> this
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

    private fun NetworkResult<LocalTokenResponseDto>.dtoOrNull(): LocalTokenResponseDto? {
        return when (this) {
            is NetworkFailure -> null
            is NetworkOk -> dto
        }
    }

    private fun addTokenToRequestHeader(token: LoginAccessToken): NetworkResult<EmptyResponseDto> {
        TODO()
    }
}