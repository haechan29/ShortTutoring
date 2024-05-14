package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult

interface Interceptor {
    suspend fun intercept(request: Request<RequestDto>): NetworkResult<ResponseDto>
}