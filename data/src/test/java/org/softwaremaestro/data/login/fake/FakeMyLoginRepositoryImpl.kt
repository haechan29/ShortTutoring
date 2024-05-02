package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.mylogin.TokenStorage
import org.softwaremaestro.data.mylogin.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.Server
import org.softwaremaestro.data.mylogin.TokenManager
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.LoginToken

class FakeMyLoginRepositoryImpl(
    private val tokenStorage: TokenStorage,
    private val validator: TokenValidator,
    private val server: Server,
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

    override suspend fun login(id: String, password: String) {
        val request = FakeRequestBuilder.build(id, password)
        server.send(request)
    }

    override suspend fun autologin() {
        tokenManager.authAccessToken()
    }
}