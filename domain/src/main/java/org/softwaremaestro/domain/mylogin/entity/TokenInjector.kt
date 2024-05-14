package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult

interface TokenInjector {
    suspend fun injectToken(request: Request<RequestDto>): NetworkResult<EmptyResponseDto>
}