package org.softwaremaestro.data.fake_login.legacy

import org.softwaremaestro.domain.fake_login.result.NetworkResult

interface LoginTokenIssuer {
    suspend fun issueToken(): NetworkResult<Unit>
}

interface AccessTokenIssuer: LoginTokenIssuer
interface RefreshTokenIssuer: LoginTokenIssuer