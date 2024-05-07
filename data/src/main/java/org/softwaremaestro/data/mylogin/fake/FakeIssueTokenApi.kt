package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.IssueTokenApi
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.RequestDto
import org.softwaremaestro.domain.mylogin.entity.Server

object FakeIssueAccessTokenApi: IssueTokenApi {
    override val server: Server = FakeServer

    override suspend fun issueToken(): NetworkResult<Any> {
        return Ok(Unit)
    }

    override fun toRequest(dto: RequestDto): Request {
        TODO("Not yet implemented")
    }
}

object FakeIssueRefreshTokenApi: IssueTokenApi {
    override val server: Server = FakeServer

    override suspend fun issueToken(): NetworkResult<Any> {
        return Ok(Unit)
    }

    override fun toRequest(dto: RequestDto): Request {
        TODO("Not yet implemented")
    }
}