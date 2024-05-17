package org.softwaremaestro.data.fake_login.legacy

import org.softwaremaestro.domain.fake_login.LoginTokenRepository
import org.softwaremaestro.domain.fake_login.result.AuthResult
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import javax.inject.Inject

class LoginTokenRepositoryImpl @Inject constructor(
    private val loginTokenAuthenticator: LoginTokenAuthenticator,
    private val accessTokenIssuer: AccessTokenIssuer,
    private val refreshTokenIssuer: RefreshTokenIssuer
): LoginTokenRepository {
    override suspend fun authLoginToken(): AuthResult {
        return loginTokenAuthenticator.authLoginToken()
    }

    override suspend fun issueAccessToken(): NetworkResult<Unit> {
        return accessTokenIssuer.issueToken()
    }

    override suspend fun issueRefreshToken(): NetworkResult<Unit> {
        return refreshTokenIssuer.issueToken()
    }
}