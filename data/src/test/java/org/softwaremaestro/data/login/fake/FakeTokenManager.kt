package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.mylogin.AccessToken
import org.softwaremaestro.data.mylogin.LocalDB
import org.softwaremaestro.data.mylogin.TokenManager

class FakeTokenManager(private val localDB: LocalDB): TokenManager {
    override suspend fun authAccessToken() {
        val token = localDB.readAccessToken()
        if (token == null) {
            authRefreshToken()
            return
        }
    }

    override suspend fun authRefreshToken() {

    }
}