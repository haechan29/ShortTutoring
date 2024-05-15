package org.softwaremaestro.data.fake_login.legacy

import org.softwaremaestro.data.fake_login.dto.AutoLoginResponseDto
import org.softwaremaestro.data.fake_login.dto.EmptyRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueLoginTokenRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueTokenResponseDto
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.data.fake_login.dto.ResponseDto
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.result.NetworkResult

interface Server<in ReqDto : RequestDto, out ResDto : ResponseDto> {
    suspend fun send(request: Request<ReqDto>): Response<ResDto>
}

interface IssueLoginTokenServer: Server<IssueLoginTokenRequestDto, IssueTokenResponseDto>
interface IssueAccessTokenServer: IssueLoginTokenServer
interface IssueRefreshTokenServer: IssueLoginTokenServer

interface AutoLoginServer: Server<EmptyRequestDto, AutoLoginResponseDto>

interface Request<out Dto: RequestDto> {
    val dto: Dto
}

interface Response<out Dto: ResponseDto> {
    val body: NetworkResult<Dto>
}