package org.softwaremaestro.data.fake_login.legacy

import org.softwaremaestro.domain.fake_login.entity.LoginToken

interface LoginTokenStorage {
    suspend fun save(loginToken: LoginToken)
    suspend fun load(): LoginToken?
    suspend fun clear()
}

interface AccessTokenStorage: LoginTokenStorage
interface RefreshTokenStorage: LoginTokenStorage