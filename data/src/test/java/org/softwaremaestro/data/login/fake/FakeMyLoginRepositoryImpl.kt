package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.login.TokenStorage
import org.softwaremaestro.data.login.TokenValidator
import org.softwaremaestro.domain.login.MyLoginRepository
import org.softwaremaestro.domain.login.entity.LoginToken

class FakeMyLoginRepositoryImpl(val tokenStorage: TokenStorage, val validator: TokenValidator): MyLoginRepository {
    override suspend fun save(token: LoginToken) {
        validator.validate(token)

        tokenStorage.save(token)
    }

    override suspend fun load(): LoginToken {
        val token = tokenStorage.load()

        validator.validate(token)

        return token
    }
}