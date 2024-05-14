package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult

interface TokenIssuer<Token: LoginToken> {
    suspend fun issueToken(): NetworkResult<EmptyResponseDto>
}