package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.RequestDto
import org.softwaremaestro.domain.mylogin.entity.Server

abstract class FakeApi: Api {
    override val server: Server = FakeServer

    override fun toRequest(dto: RequestDto): Request {
        TODO("Not yet implemented")
    }
}