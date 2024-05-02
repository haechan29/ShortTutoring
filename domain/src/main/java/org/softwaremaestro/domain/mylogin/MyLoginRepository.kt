package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface MyLoginRepository {
    suspend fun save(token: LoginToken)
    suspend fun load(): LoginToken?
    suspend fun login(id: String, password: String)
    suspend fun autologin()
}