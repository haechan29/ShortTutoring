package org.softwaremaestro.domain.login

import org.softwaremaestro.domain.login.entity.LoginToken

interface MyLoginRepository {
    suspend fun save(token: LoginToken)
    suspend fun load(): LoginToken
}