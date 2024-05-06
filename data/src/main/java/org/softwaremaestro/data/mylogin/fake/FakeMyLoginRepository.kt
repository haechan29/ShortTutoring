package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.Ok

object FakeLoginApi: LoginApi {
    override suspend fun login(): NetworkResult<Any> {
        return Ok(Unit)
    }
}

object FakeMyLoginRepository: MyLoginRepository {
    override suspend fun login(id: String, password: String): NetworkResult<Any> {
        val dto = LoginRequestDto(id, password)
        if (!dto.isValid()) {
            return AccessTokenNotFound
        }

        return FakeLoginApi.login()
    }

    override suspend fun autologin() {
        when (val result = FakeAccessTokenRepository.load()) {
            is Ok -> return
            is Failure -> {
                result.message // handle message
                FakeRefreshTokenRepository.load()
            }
        }
    }
}