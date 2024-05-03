package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.LoginResult

interface MyLoginRepository {
    suspend fun login(id: String, password: String): LoginResult
    suspend fun autologin()
}