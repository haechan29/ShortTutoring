package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.mylogin.LocalDB
import org.softwaremaestro.data.mylogin.TokenManager

class FakeTokenManager(
    private val localDB: LocalDB,
//    private val validator: TokenValidator
): TokenManager {
    override suspend fun authAccessToken() {
        val token = localDB.readAccessToken()
        if (token == null) {
            authRefreshToken()
            return
        }
//        if (hasAccessToken()) {
//            validator.validate(token!!)
//        }
    }

    override suspend fun authRefreshToken() {

    }

    override suspend fun hasAccessToken(): Boolean {
        return false
    }
}