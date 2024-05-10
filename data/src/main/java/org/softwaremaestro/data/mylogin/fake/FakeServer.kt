package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Server

object FakeServer: Server {
    override suspend fun send(request: Request): NetworkResult<ResponseDto> {
        TODO("Not yet implemented")
    }
}