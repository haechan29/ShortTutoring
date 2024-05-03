package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.TokenStorage
import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.LoginResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken

class FakeMyLoginRepositoryImpl(
    private val api: Api,
    private val tokenRepository: TokenRepository
): MyLoginRepository {
    override suspend fun login(id: String, password: String): LoginResult {
        val dto = LoginRequestDto(id, password)
        if (!dto.isValid()) {
            return LoginResult.INVALID_LOGIN_INFO
        }

        api.send(dto)

        return LoginResult.OK
    }

    override suspend fun autologin() {
        tokenRepository.authAccessToken()
    }
}