package org.softwaremaestro.data.login.fake

import org.softwaremaestro.domain.mylogin.entity.TokenStorage
import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.TokenManager
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.LoginResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken

class FakeMyLoginRepositoryImpl(
    private val tokenStorage: TokenStorage,
    private val validator: TokenValidator,
    private val api: Api,
    private val tokenManager: TokenManager
): MyLoginRepository {
    override suspend fun save(token: LoginToken) {
        val isValid = validator.isValid(token)

        if (isValid) {
            tokenStorage.save(token)
        }
    }

    override suspend fun load(): LoginToken? {
        val token = tokenStorage.load() ?: return null

        val isValid = validator.isValid(token)

        return if (isValid) token else null
    }

    override suspend fun login(id: String, password: String): LoginResult {
        val dto = LoginRequestDto(id, password)
        if (!dto.isValid()) {
            return LoginResult.INVALID_LOGIN_INFO
        }

        api.send(dto)

        return LoginResult.OK
    }

    override suspend fun autologin() {
        tokenManager.authAccessToken()
    }
}