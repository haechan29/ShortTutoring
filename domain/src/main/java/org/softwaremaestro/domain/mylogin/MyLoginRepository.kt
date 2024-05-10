package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkResult

interface MyLoginRepository {
    suspend fun login(id: String, password: String): NetworkResult<EmptyResponseDto>
    suspend fun autologin(): NetworkResult<EmptyResponseDto>
}