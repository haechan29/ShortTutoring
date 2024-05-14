package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface TokenRepository<Token: LoginToken> {
    suspend fun save(token: Token): NetworkResult<EmptyResponseDto>
    suspend fun load(): NetworkResult<LocalTokenResponseDto>
}