package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.AuthTokenApi
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok

object FakeAuthAccessTokenApi: AuthTokenApi {
    override suspend fun authToken(): NetworkResult<Any> {
        return Ok(Unit)
    }
}