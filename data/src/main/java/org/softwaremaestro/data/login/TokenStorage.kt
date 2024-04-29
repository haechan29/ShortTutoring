package org.softwaremaestro.data.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.domain.login.entity.TokenNotFoundException

interface TokenStorage {
    suspend fun save(token: LoginToken)
    suspend fun load(): LoginToken
    suspend fun clear()
}