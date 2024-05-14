package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.util.dtoOrNull
import org.softwaremaestro.data.mylogin.util.nullIfSuccess
import org.softwaremaestro.domain.mylogin.entity.Interceptor
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.Response
import org.softwaremaestro.domain.mylogin.entity.Server
import org.softwaremaestro.domain.mylogin.entity.TokenInjector
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.result.DtoContainsNullFieldFailure
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.result.NetworkSuccess

class FakeInterceptor(
    private val tokenInjector: TokenInjector,
    private val server: Server
): Interceptor {
    private val accessTokenNotFound = AccessTokenNotFound

    override suspend fun intercept(request: Request<RequestDto>): NetworkResult<ResponseDto> {
        injectToken(request).nullIfSuccess()?.let { return accessTokenNotFound }

        val body = sendToServer(request).body

        if (body is NetworkSuccess && body.dto.containsNullField()) return DtoContainsNullFieldFailure

        return body
    }

    private suspend fun injectToken(dto: Request<RequestDto>): NetworkResult<EmptyResponseDto> {
        return tokenInjector.injectToken(dto)
    }

    private suspend fun sendToServer(request: Request<RequestDto>): Response<ResponseDto> {
        return server.send(request)
    }
}