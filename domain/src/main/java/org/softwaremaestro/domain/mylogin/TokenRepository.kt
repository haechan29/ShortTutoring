package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.AttemptResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface TokenRepository<T> {
    suspend fun authAccessToken(): AttemptResult<T>
    suspend fun authRefreshToken(): AttemptResult<T>

    suspend fun save(token: LoginToken)
    suspend fun load(): LoginToken?
}