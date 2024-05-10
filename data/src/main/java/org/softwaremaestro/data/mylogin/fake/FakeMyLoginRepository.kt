package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.Ok

abstract class FakeMyLoginRepository(private val api: LoginApi): MyLoginRepository {
    override suspend fun login(id: String, password: String): NetworkResult<EmptyResponseDto> {
        val dto = LoginRequestDto(id, password)
        if (!isValid(dto)) return InvalidLoginInfo

        return when (val result = api.sendRequest(dto)) {
            is Ok -> Ok(EmptyResponseDto)
            is Failure -> result
        }
    }

    protected abstract fun isValid(dto: LoginRequestDto): Boolean
}