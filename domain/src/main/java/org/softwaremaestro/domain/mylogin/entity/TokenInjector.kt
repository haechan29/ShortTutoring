package org.softwaremaestro.domain.mylogin.entity

interface TokenInjector {
    suspend fun injectToken(request: Request): NetworkResult<EmptyResponseDto>
}