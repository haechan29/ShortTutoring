package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult

interface MyLoginRepository {
    suspend fun login(id: String, password: String): NetworkResult<LoginResponseDto>
    suspend fun autologin(): NetworkResult<EmptyResponseDto>
}