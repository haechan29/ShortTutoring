package org.softwaremaestro.data.fake_login.legacy

import org.softwaremaestro.data.fake_login.dto.AutoLoginResponseDto
import org.softwaremaestro.data.fake_login.dto.EmptyRequestDto
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.data.fake_login.dto.ResponseDto
import org.softwaremaestro.domain.fake_login.result.NetworkResult

interface Interceptor<in ReqDto: RequestDto, out ResDto: ResponseDto> {
    suspend fun intercept(request: Request<ReqDto>): NetworkResult<ResDto>
}

interface AutoLoginInterceptor: Interceptor<EmptyRequestDto, AutoLoginResponseDto>