package org.softwaremaestro.data.login.fake

import org.softwaremaestro.domain.mylogin.entity.AccessLoginToken
import org.softwaremaestro.data.mylogin.LocalDB

object FakeLocalDB: LocalDB {
    override suspend fun readAccessToken(): AccessLoginToken? {
        return null
    }
}