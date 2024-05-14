package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.dto.EmptyRequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult

interface Api<in ReqDto: RequestDto, out ResDto: ResponseDto> {
    suspend fun sendRequest(dto: ReqDto): NetworkResult<ResDto>
}

interface LoginApi: Api<LoginRequestDto, LoginResponseDto>
interface AutoLoginApi: Api<EmptyRequestDto, EmptyResponseDto>
interface IssueTokenApi: Api<LoginRequestDto, LoginResponseDto>