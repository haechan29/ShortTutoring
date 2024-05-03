package org.softwaremaestro.data.login.fake

import org.softwaremaestro.domain.mylogin.entity.LocalDB
import org.softwaremaestro.domain.mylogin.entity.TokenManager
import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.Api

class FakeTokenManager(
    private val localDB: LocalDB,
    private val validator: TokenValidator,
    private val api: Api
): TokenManager {
    override suspend fun authAccessToken() {
        val token = localDB.readAccessToken()
        if (token == null) {
            authRefreshToken()
            return
        }

        if (!validator.isValid(token)) {
            authRefreshToken()
            return
        }

//        api.send(token)
    }

    override suspend fun authRefreshToken() {

    }
}