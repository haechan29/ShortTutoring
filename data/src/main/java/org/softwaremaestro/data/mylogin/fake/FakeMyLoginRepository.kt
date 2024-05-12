package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.data.mylogin.util.dtoOrNull
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkSuccess

class FakeMyLoginRepository(private val api: LoginApi): MyLoginRepository {
    override suspend fun login(id: String, password: String): NetworkResult<EmptyResponseDto> {
        val requestDto = LoginRequestDto(id, password)
        if (!isValid(requestDto)) return InvalidLoginInfo

        val result = sendRequest(requestDto)
//        val responseDto = result.dtoOrNull() ?: return result as NetworkFailure
//
//        // handle
//
//        return result.erase
        return NetworkSuccess(EmptyResponseDto)
    }

    override suspend fun autologin(): NetworkResult<EmptyResponseDto> {
        TODO()
    }

    private fun isValid(dto: LoginRequestDto): Boolean {
        TODO()
    }

    private suspend fun sendRequest(dto: LoginRequestDto): NetworkResult<LoginResponseDto> {
        return api.sendRequest(dto)
    }
}