package org.softwaremaestro.domain.fake_login

import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.result.NetworkResult

interface LoginTokenDao {
    suspend fun save(token: LoginToken): NetworkResult<Unit>
    suspend fun load(): NetworkResult<LoginToken>
}

interface AccessTokenDao : LoginTokenDao
interface RefreshTokenDao: LoginTokenDao