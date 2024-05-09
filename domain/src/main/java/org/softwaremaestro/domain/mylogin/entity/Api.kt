package org.softwaremaestro.domain.mylogin.entity

interface Api<Dto: ResponseDto> {
    suspend fun sendRequest(dto: RequestDto): NetworkResult<Dto>
}

interface LoginApi: Api<LoginResponseDto>
interface IssueTokenApi: Api<IssueTokenResponseDto>