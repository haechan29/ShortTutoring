package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.mylogin.TokenStorage
import org.softwaremaestro.data.mylogin.TokenValidator
import org.softwaremaestro.data.mylogin.Server
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
        validator.validate(token)

        tokenStorage.save(token)
    }

    override suspend fun load(): LoginToken {
        val token = tokenStorage.load()

        validator.validate(token)

        return token
    }

    override suspend fun login(id: String, password: String) {
        val request = FakeRequestBuilder.build(id, password)
        server.send(request)
    }

    override suspend fun autologin() {
        tokenManager.authAccessToken()
    }
}