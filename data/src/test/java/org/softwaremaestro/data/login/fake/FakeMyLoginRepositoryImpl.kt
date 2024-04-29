package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.login.LoginValidator
import org.softwaremaestro.data.login.TokenStorage
import org.softwaremaestro.data.login.TokenValidator
import org.softwaremaestro.data.login.Server
import org.softwaremaestro.domain.login.entity.exception.InvalidIdException
import org.softwaremaestro.domain.login.entity.exception.InvalidPasswordException
import org.softwaremaestro.domain.login.MyLoginRepository
import org.softwaremaestro.domain.login.entity.LoginToken

class FakeMyLoginRepositoryImpl(
    private val tokenStorage: TokenStorage,
    private val validator: TokenValidator,
    private val server: Server
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
}