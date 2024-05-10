package org.softwaremaestro.domain.mylogin.entity

interface TokenIssuer {
    suspend fun issueToken(): NetworkResult<EmptyResponseDto>
}