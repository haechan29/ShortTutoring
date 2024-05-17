package org.softwaremaestro.domain.fake_login

import org.softwaremaestro.domain.fake_login.result.AuthResult
import org.softwaremaestro.domain.fake_login.result.NetworkResult

interface LoginTokenRepository {
    suspend fun authLoginToken(): AuthResult
    suspend fun issueAccessToken(): NetworkResult<Unit>
    suspend fun issueRefreshToken(): NetworkResult<Unit>
}