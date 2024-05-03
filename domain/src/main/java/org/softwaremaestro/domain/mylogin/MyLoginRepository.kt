package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.AttemptResult

interface MyLoginRepository<T> {
    suspend fun login(id: String, password: String): AttemptResult<T>
    suspend fun autologin()
}