package org.softwaremaestro.data.fake_login.fake

import org.softwaremaestro.data.fake_login.dto.AutoLoginResponseDto
import org.softwaremaestro.data.fake_login.dto.EmptyRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueLoginTokenRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueTokenResponseDto
import org.softwaremaestro.domain.fake_login.util.attemptUntilSuccess
import org.softwaremaestro.domain.fake_login.util.dtoOrNull
import org.softwaremaestro.data.fake_login.legacy.Request
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.data.fake_login.dto.ResponseDto
import org.softwaremaestro.data.fake_login.legacy.Api
import org.softwaremaestro.data.fake_login.legacy.AutoLoginApi
import org.softwaremaestro.data.fake_login.legacy.AutoLoginInterceptor
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenServer
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenServer
import org.softwaremaestro.data.fake_login.legacy.IssueLoginTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueLoginTokenServer
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.domain.fake_login.result.DtoContainsNullFieldFailure
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import javax.inject.Inject

abstract class FakeApi<in ReqDto: RequestDto, out ResDto: ResponseDto>: Api<ReqDto, ResDto> {
    override suspend fun sendRequest(dto: ReqDto): NetworkResult<ResDto> {
        val request = toRequest(dto)
        return attemptUntilSuccess(3) {
            val result = sendToServer(request)

            val dto = result.dtoOrNull() ?: return@attemptUntilSuccess result as NetworkFailure

            if (dto.containsNullField()) return@attemptUntilSuccess DtoContainsNullFieldFailure

            result
        }
    }

    private fun toRequest(dto: ReqDto): Request<ReqDto> {
        return FakeRequest(dto)
    }

    abstract suspend fun sendToServer(request: Request<ReqDto>): NetworkResult<ResDto>
}

abstract class FakeIssueLoginTokenApi(
    private val server: IssueLoginTokenServer
): FakeApi<IssueLoginTokenRequestDto, IssueTokenResponseDto>(), IssueLoginTokenApi {
    override suspend fun sendRequest(dto: IssueLoginTokenRequestDto): NetworkResult<IssueTokenResponseDto> {
        return super.sendRequest(dto)
    }

    override suspend fun sendToServer(request: Request<IssueLoginTokenRequestDto>): NetworkResult<IssueTokenResponseDto> {
        return server.send(request).body
    }
}

class FakeIssueAccessTokenApi @Inject constructor(
    server: IssueAccessTokenServer
): FakeIssueLoginTokenApi(server), IssueAccessTokenApi

class FakeIssueRefreshTokenApi @Inject constructor(
    server: IssueRefreshTokenServer
): FakeIssueLoginTokenApi(server), IssueRefreshTokenApi

class FakeAutoLoginApi @Inject constructor(
    private val interceptor: AutoLoginInterceptor
): FakeApi<EmptyRequestDto, AutoLoginResponseDto>(), AutoLoginApi {
    override suspend fun sendRequest(dto: EmptyRequestDto): NetworkResult<AutoLoginResponseDto> {
        return super.sendRequest(dto)
    }

    override suspend fun sendToServer(request: Request<EmptyRequestDto>): NetworkResult<AutoLoginResponseDto> {
        return interceptor.intercept(request)
    }
}