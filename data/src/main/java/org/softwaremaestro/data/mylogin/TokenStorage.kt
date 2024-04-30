package org.softwaremaestro.data.mylogin

import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface TokenStorage {
    suspend fun save(token: LoginToken)
    suspend fun load(): LoginToken
    suspend fun clear()
}