package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface TokenRepository {
    suspend fun authAccessToken()
    suspend fun authRefreshToken()

    suspend fun save(token: LoginToken)
    suspend fun load(): LoginToken?
}