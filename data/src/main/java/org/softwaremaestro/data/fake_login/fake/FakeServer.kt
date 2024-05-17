package org.softwaremaestro.data.fake_login.fake

import org.softwaremaestro.data.fake_login.dto.AutoLoginResponseDto
import org.softwaremaestro.data.fake_login.dto.EmptyRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueLoginTokenRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueTokenResponseDto
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.data.fake_login.dto.ResponseDto
import org.softwaremaestro.data.fake_login.legacy.AutoLoginServer
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenServer
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenServer
import org.softwaremaestro.data.fake_login.legacy.IssueLoginTokenServer
import org.softwaremaestro.data.fake_login.legacy.Request
import org.softwaremaestro.data.fake_login.legacy.Response
import org.softwaremaestro.data.fake_login.legacy.Server
import org.softwaremaestro.domain.fake_login.entity.FakeLoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.FakeLoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.entity.Role
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import javax.inject.Inject

abstract class FakeServer<in ReqDto : RequestDto, out ResDto : ResponseDto>: Server<ReqDto, ResDto> {
    override suspend fun send(request: Request<ReqDto>): Response<ResDto> {
        val resDto = toResDto(request.dto)
        return toResponse(NetworkSuccess(resDto))
    }

    protected abstract fun toResDto(reqDto: ReqDto): ResDto

    private fun toResponse(result: NetworkResult<ResDto>): Response<ResDto> {
        return FakeResponse(result)
    }
}

data class FakeRequest<out Dto: RequestDto>(
    override val header: MutableMap<String, LoginToken> = mutableMapOf(),
    override val dto: Dto
): Request<Dto>

class FakeResponse<out Dto: ResponseDto>(override val body: NetworkResult<Dto>): Response<Dto>

abstract class FakeIssueLoginTokenServer
: FakeServer<IssueLoginTokenRequestDto, IssueTokenResponseDto>(), IssueLoginTokenServer {
    override fun toResDto(reqDto: IssueLoginTokenRequestDto): IssueTokenResponseDto {
        return IssueTokenResponseDto(FakeLoginAccessToken, FakeLoginRefreshToken)
    }
}

class FakeIssueAccessTokenServer @Inject constructor(): FakeIssueLoginTokenServer(), IssueAccessTokenServer
class FakeIssueRefreshTokenServer @Inject constructor(): FakeIssueLoginTokenServer(), IssueRefreshTokenServer

class FakeAutoLoginServer @Inject constructor(): FakeServer<EmptyRequestDto, AutoLoginResponseDto>(), AutoLoginServer {
    override fun toResDto(reqDto: EmptyRequestDto): AutoLoginResponseDto {
        return AutoLoginResponseDto(Role.STUDENT)
    }
}