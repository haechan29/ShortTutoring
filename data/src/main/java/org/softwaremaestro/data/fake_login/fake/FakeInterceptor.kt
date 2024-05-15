package org.softwaremaestro.data.fake_login.fake

import org.softwaremaestro.data.fake_login.dto.AutoLoginResponseDto
import org.softwaremaestro.data.fake_login.dto.EmptyRequestDto
import org.softwaremaestro.domain.fake_login.util.nullIfSuccess
import org.softwaremaestro.data.fake_login.legacy.Interceptor
import org.softwaremaestro.data.fake_login.legacy.Request
import org.softwaremaestro.data.fake_login.legacy.Response
import org.softwaremaestro.data.fake_login.legacy.Server
import org.softwaremaestro.data.fake_login.dto.EmptyResponseDto
import org.softwaremaestro.data.fake_login.legacy.TokenInjector
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.data.fake_login.dto.ResponseDto
import org.softwaremaestro.data.fake_login.legacy.AutoLoginInterceptor
import org.softwaremaestro.data.fake_login.legacy.AutoLoginServer
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.domain.fake_login.result.AccessTokenNotFound
import org.softwaremaestro.domain.fake_login.result.DtoContainsNullFieldFailure
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess

abstract class FakeInterceptor<in ReqDto: RequestDto, out ResDto: ResponseDto>(
    private val tokenInjector: TokenInjector,
    private val server: Server<ReqDto, ResDto>
): Interceptor<ReqDto, ResDto> {
    private val accessTokenNotFound = AccessTokenNotFound

    override suspend fun intercept(request: Request<ReqDto>): NetworkResult<ResDto> {
        injectToken(request).nullIfSuccess()?.let { return accessTokenNotFound }

        val body = sendToServer(request).body

        if (body is NetworkSuccess && body.dto.containsNullField()) return DtoContainsNullFieldFailure

        return body
    }

    private suspend fun injectToken(dto: Request<ReqDto>): NetworkResult<EmptyResponseDto> {
        return tokenInjector.injectToken(dto)
    }

    private suspend fun sendToServer(request: Request<ReqDto>): Response<ResDto> {
        return server.send(request)
    }
}

class FakeAutoLoginInterceptor(
    tokenInjector: TokenInjector, server: AutoLoginServer
): FakeInterceptor<EmptyRequestDto, AutoLoginResponseDto>(tokenInjector, server), AutoLoginInterceptor