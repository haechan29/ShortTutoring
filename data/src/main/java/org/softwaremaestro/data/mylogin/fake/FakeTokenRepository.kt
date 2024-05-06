package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.InvalidAccessToken
import org.softwaremaestro.domain.mylogin.entity.InvalidRefreshToken
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

abstract class FakeTokenRepository: TokenRepository {
    override val tokenStorage: TokenStorage = FakeTokenStorage

    override suspend fun save(token: LoginToken): NetworkResult<Any> {
        if (!token.isValid()) return invalidTokenFailure

        tokenStorage.save(token)

        return Ok(Unit)
    }

    override suspend fun load(): NetworkResult<LoginToken> {
        val token = tokenStorage.load()
        with (token) {
            if (this == null) return tokenNotFoundFailure
            if (!isValid()) return invalidTokenFailure
        }

        return Ok(token!!)
    }
}

object FakeAccessTokenRepository: FakeTokenRepository() {
    override val tokenNotFoundFailure: TokenNotFound = AccessTokenNotFound
    override val invalidTokenFailure: InvalidToken = InvalidAccessToken
}

object FakeRefreshTokenRepository: FakeTokenRepository() {
    override val tokenNotFoundFailure: TokenNotFound = RefreshTokenNotFound
    override val invalidTokenFailure: InvalidToken = InvalidRefreshToken
}