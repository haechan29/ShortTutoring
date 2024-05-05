package org.softwaremaestro.domain.mylogin

import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

interface TokenRepository {
    val tokenNotFoundFailure: TokenNotFound
    val invalidTokenFailure: InvalidToken

    val tokenStorage: TokenStorage
    val tokenAuthenticator: TokenAuthenticator

    suspend fun authToken(): NetworkResult<Any>

    suspend fun save(token: LoginToken): NetworkResult<Any>
    suspend fun load(): NetworkResult<LoginToken>
}