package org.softwaremaestro.domain.fake_login

import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.LoginSubToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.result.NetworkResult

interface LoginTokenStorageRepository {
    suspend fun save(token: LoginToken): NetworkResult<Unit>
    suspend fun load(): NetworkResult<LoginToken>
}

interface AccessTokenStorageRepository : LoginTokenStorageRepository
interface RefreshTokenStorageRepository: LoginTokenStorageRepository