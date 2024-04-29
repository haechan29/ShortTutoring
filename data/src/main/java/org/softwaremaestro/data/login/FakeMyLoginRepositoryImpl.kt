package org.softwaremaestro.data.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.softwaremaestro.domain.login.MyLoginRepository
import org.softwaremaestro.domain.login.entity.InvalidTokenException
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.domain.login.entity.TokenNotFoundException
import javax.inject.Inject

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