package org.softwaremaestro.data.fake_login.impl

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.softwaremaestro.data.fake_login.legacy.AccessTokenStorage
import org.softwaremaestro.data.fake_login.legacy.LoginTokenStorage
import org.softwaremaestro.data.fake_login.legacy.RefreshTokenStorage
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import javax.inject.Inject

open class FakeLoginTokenStorage: LoginTokenStorage {
    private var savedToken: LoginToken? = null

    override suspend fun save(loginToken: LoginToken) {
        withContext(Dispatchers.IO) {
            savedToken = loginToken
            delay(50)
        }
    }

    override suspend fun load(): LoginToken? {
        return withContext(Dispatchers.IO) {
            delay(50)
            savedToken
        }
    }

    override suspend fun clear() {
        savedToken = null
    }
}

class FakeAccessTokenStorage @Inject constructor(): FakeLoginTokenStorage(), AccessTokenStorage
class FakeRefreshTokenStorage @Inject constructor(): FakeLoginTokenStorage(), RefreshTokenStorage