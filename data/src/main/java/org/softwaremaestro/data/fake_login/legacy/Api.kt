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

interface Api<in ReqDto: RequestDto, out ResDto: ResponseDto> {
    suspend fun sendRequest(dto: ReqDto): NetworkResult<ResDto>
}

interface IssueLoginTokenApi: Api<IssueLoginTokenRequestDto, IssueTokenResponseDto>
interface IssueAccessTokenApi: IssueLoginTokenApi
interface IssueRefreshTokenApi: IssueLoginTokenApi

interface AutoLoginApi: Api<EmptyRequestDto, AutoLoginResponseDto>