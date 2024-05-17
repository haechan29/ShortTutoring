package org.softwaremaestro.data.fake_login.fake

import android.util.Log
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
import org.softwaremaestro.data.fake_login.legacy.LoginTokenInjector
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.data.fake_login.dto.EmptyResponseDto
import org.softwaremaestro.domain.fake_login.AccessTokenDao
import org.softwaremaestro.domain.fake_login.LoginTokenRepository
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken.Companion.ACCESS_TOKEN
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import javax.inject.Inject

class FakeTokenInjector @Inject constructor(
    private val accessTokenDao: AccessTokenDao,
    private val loginTokenRepository: LoginTokenRepository
): LoginTokenInjector {
    override suspend fun injectLoginToken(request: Request<RequestDto>): NetworkResult<Unit> {
        checkTokenOrFail()?.let { failure -> return failure }

        val accessToken = loadAccessToken().dtoOrNull() ?: return AccessTokenNotFound

        addAccessTokenToRequestHeader(request, accessToken)

        return NetworkSuccess(Unit)
    }

    private suspend fun checkTokenOrFail(): NetworkFailure? {
        val authFailure = authenticateToken().nullIfSuccess() ?: return null

        return issueToken(authFailure).nullIfSuccess()
    }

    private suspend fun authenticateToken(): AuthResult {
        return loginTokenRepository.authLoginToken()
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
        return loginTokenRepository.issueAccessToken()
    }

    private suspend fun issueRefreshToken(): NetworkResult<Unit> {
        return loginTokenRepository.issueRefreshToken()
    }

    private suspend fun loadAccessToken(): NetworkResult<LoginAccessToken> {
        return accessTokenDao.load() as NetworkResult<LoginAccessToken>
    }

    private fun addAccessTokenToRequestHeader(request: Request<RequestDto>, accessToken: LoginAccessToken) {
        request.header[ACCESS_TOKEN] = accessToken
    }
}