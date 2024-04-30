package org.softwaremaestro.data.login.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.data.mylogin.AccessToken
import org.softwaremaestro.data.mylogin.LocalDB
import org.softwaremaestro.domain.mylogin.exception.InvalidAccessTokenException

object FakeLocalDB: LocalDB {
    override suspend fun readAccessToken(): AccessToken? {
        return null
    }
}