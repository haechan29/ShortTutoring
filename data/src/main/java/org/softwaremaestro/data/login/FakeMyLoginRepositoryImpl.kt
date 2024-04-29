package org.softwaremaestro.data.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.softwaremaestro.domain.login.MyLoginRepository
import org.softwaremaestro.domain.login.entity.InvalidTokenException
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.domain.login.entity.TokenNotFoundException
import javax.inject.Inject

object FakeMyLoginRepositoryImpl: MyLoginRepository {
    override suspend fun save(token: LoginToken) {
        if (!token.isValid()) {
            throw InvalidTokenException
        }

        FakeTokenStorage.save(token)
    }

    override suspend fun load(): LoginToken {
        val token = FakeTokenStorage.load()

        if (!token.isValid()) {
            throw InvalidTokenException
        }

        return token
    }
}