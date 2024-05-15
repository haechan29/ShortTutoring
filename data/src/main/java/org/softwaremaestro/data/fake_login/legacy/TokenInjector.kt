package org.softwaremaestro.data.fake_login.legacy

import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.data.fake_login.dto.EmptyResponseDto

interface TokenInjector {
    suspend fun injectToken(request: Request<RequestDto>): NetworkResult<EmptyResponseDto>
}