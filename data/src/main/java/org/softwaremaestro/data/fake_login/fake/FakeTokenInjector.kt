package org.softwaremaestro.data.fake_login.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.domain.fake_login.util.NetworkSyncQueue.Companion.sync
import org.softwaremaestro.domain.fake_login.util.dtoOrNull
import org.softwaremaestro.domain.fake_login.util.nullIfSuccess
import org.softwaremaestro.domain.fake_login.result.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.fake_login.result.AccessTokenNotFound
import org.softwaremaestro.domain.fake_login.result.AuthFailure
import org.softwaremaestro.domain.fake_login.result.AuthResult
import org.softwaremaestro.data.fake_login.legacy.Request
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.data.fake_login.legacy.LoginTokenAuthenticator
import org.softwaremaestro.data.fake_login.legacy.TokenInjector
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.data.fake_login.dto.EmptyResponseDto
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenRepository
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenRepository
import org.softwaremaestro.domain.fake_login.AccessTokenStorageRepository
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import javax.inject.Inject

class FakeTokenInjector @Inject constructor(
    private val loginTokenAuthenticator: LoginTokenAuthenticator,
    private val accessTokenStorageRepository: AccessTokenStorageRepository,
    private val issueAccessTokenRepository: IssueAccessTokenRepository,
    private val issueRefreshTokenRepository: IssueRefreshTokenRepository,
): TokenInjector {
    private val accessTokenNotFound: AccessTokenNotFound = AccessTokenNotFound

    override suspend fun injectToken(request: Request<RequestDto>): NetworkResult<EmptyResponseDto> {
        checkTokenOrFail()?.let { failure -> return failure }

        val token = loadAccessToken().dtoOrNull() ?: return accessTokenNotFound

        return addTokenToRequestHeader(token)
    }

    private suspend fun checkTokenOrFail(): NetworkFailure? {
        val authFailure = authenticateToken().nullIfSuccess() ?: return null

        return issueToken(authFailure).nullIfSuccess()
    }

    private suspend fun authenticateToken(): AuthResult {
        return loginTokenAuthenticator.authLoginToken()
    }

    private suspend fun issueToken(authFailure: AuthFailure): NetworkResult<Unit> {
        return sync { issueTokenFromServer(authFailure) }
    }

    private suspend fun issueTokenFromServer(authFailure: AuthFailure): NetworkResult<Unit> {
        delay(100)
        return when (authFailure) {
            is AccessTokenIsNotAuthenticated -> issueAccessToken()
            is RefreshTokenIsNotAuthenticated -> issueRefreshToken()
        }
    }

    private suspend fun issueAccessToken(): NetworkResult<Unit> {
        return issueAccessTokenRepository.issueToken()
    }

    private suspend fun issueRefreshToken(): NetworkResult<Unit> {
        return issueRefreshTokenRepository.issueToken()
    }

    private suspend fun loadAccessToken(): NetworkResult<LoginToken> {
        return accessTokenStorageRepository.load()
    }

    private fun addTokenToRequestHeader(loginToken: LoginToken): NetworkResult<EmptyResponseDto> {
        TODO()
    }
}