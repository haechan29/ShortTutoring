package org.softwaremaestro.domain.mylogin.entity

interface TokenIssuer<Token: LoginToken> {
    suspend fun issueToken(): NetworkResult<EmptyResponseDto>
}