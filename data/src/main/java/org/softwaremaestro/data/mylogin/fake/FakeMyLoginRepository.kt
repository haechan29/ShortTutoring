package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.Ok

abstract class FakeMyLoginRepository(private val api: LoginApi): MyLoginRepository {
    override suspend fun login(id: String, password: String): NetworkResult<Unit> {
        val dto = LoginRequestDto(id, password)
        if (!dto.isValid()) {
            return InvalidLoginInfo
        }

        return when (val result = api.sendRequest(dto)) {
            is Ok -> {
                val body = (result.body as ResponseDto)
                Ok(Unit)
            }
            is Failure -> result
        }
    }
}