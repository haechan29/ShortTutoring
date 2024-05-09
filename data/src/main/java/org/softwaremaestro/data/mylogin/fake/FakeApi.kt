package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.RequestDto
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Server

abstract class FakeApi(private val server: Server): Api<ResponseDto> {
    override suspend fun sendRequest(dto: RequestDto): NetworkResult<ResponseDto> {
        val request = toRequest(dto)
        addTokenToRequestHeader()
        return sendToServer(request)
    }

    // 분리
    protected abstract fun toRequest(dto: RequestDto): Request

    // 분리
    abstract suspend fun addTokenToRequestHeader()

    protected abstract suspend fun sendToServer(request: Request): NetworkResult<ResponseDto>
}