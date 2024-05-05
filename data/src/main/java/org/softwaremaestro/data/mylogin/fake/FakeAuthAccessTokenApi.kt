package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.AuthTokenApi
import org.softwaremaestro.domain.mylogin.entity.NetworkResult

object FakeAuthAccessTokenApi: AuthTokenApi {
    override suspend fun authToken(): NetworkResult<Any> {
        TODO("Not yet implemented")
    }
}