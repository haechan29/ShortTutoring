package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.AutoLoginApi
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyRequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.dto.LoginResponseDto

class FakeMyLoginRepository(
    private val loginApi: LoginApi,
    private val autoLoginApi: AutoLoginApi
): MyLoginRepository {
    override suspend fun login(id: String, password: String): NetworkResult<LoginResponseDto> {
        val requestDto = LoginRequestDto(id, password)
        return loginApi.sendRequest(requestDto)
    }

    override suspend fun autologin(): NetworkResult<EmptyResponseDto> {
        return autoLoginApi.sendRequest(EmptyRequestDto)
    }
}