package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult

interface MyLoginRepository {
    suspend fun login(id: String, password: String): NetworkResult<Unit>
    suspend fun autologin(): NetworkResult<Unit>
}