package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.util.attemptUntilSuccess
import org.softwaremaestro.data.mylogin.util.dtoOrNull
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.Interceptor
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.Response
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Server
import org.softwaremaestro.domain.mylogin.entity.result.DtoContainsNullFieldFailure
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure

abstract class FakeApi(private val interceptor: Interceptor): Api<RequestDto, ResponseDto> {
    override suspend fun sendRequest(dto: RequestDto): NetworkResult<ResponseDto> {
        val request = toRequest(dto)
        return attemptUntilSuccess(3) {
            val result = sendToServer(request)

            val dto = result.dtoOrNull() ?: return@attemptUntilSuccess result as NetworkFailure

            if (dto.containsNullField()) return@attemptUntilSuccess DtoContainsNullFieldFailure

            result
        }
    }

    private fun toRequest(dto: RequestDto): Request<RequestDto> {
        TODO()
    }

    private suspend fun sendToServer(request: Request<RequestDto>): NetworkResult<ResponseDto> {
        return interceptor.intercept(request)
    }
}