package org.softwaremaestro.data.fake_login.fake

import org.softwaremaestro.domain.fake_login.result.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.fake_login.result.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.fake_login.result.AuthResult
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.domain.fake_login.result.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.data.fake_login.legacy.LoginTokenAuthenticator
import org.softwaremaestro.domain.fake_login.AccessTokenDao
import org.softwaremaestro.domain.fake_login.RefreshTokenDao
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import javax.inject.Inject

class FakeLoginTokenAuthenticator @Inject constructor(
    private val accessTokenDao: AccessTokenDao,
    private val refreshTokenDao: RefreshTokenDao
): LoginTokenAuthenticator {
    override suspend fun authLoginToken(): AuthResult {
        if (loadAccessToken() is NetworkSuccess) {
            return AccessTokenIsAuthenticated
        }

        return when (loadRefreshToken()) {
            is NetworkSuccess -> AccessTokenIsNotAuthenticated
            is NetworkFailure -> RefreshTokenIsNotAuthenticated
        }
    }

    private suspend fun loadAccessToken(): NetworkResult<LoginToken> {
        return accessTokenDao.load()
    }

    private suspend fun loadRefreshToken(): NetworkResult<LoginToken> {
        return refreshTokenDao.load()
    }
}