package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.InvalidAccessToken
import org.softwaremaestro.domain.mylogin.entity.InvalidRefreshToken
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

abstract class FakeTokenRepository<Token: LoginToken>: TokenRepository<Token> {
    protected abstract val tokenStorage: TokenStorage<Token>

    abstract val tokenNotFoundFailure: TokenNotFound
    abstract val invalidTokenFailure: InvalidToken

    override suspend fun save(token: Token): NetworkResult<Unit> {
        if (!token.isValid()) return invalidTokenFailure

        saveToStorage(token)

        return Ok(Unit)
    }

    private suspend fun saveToStorage(token: Token) {
        return tokenStorage.save(token)
    }

    override suspend fun load(): NetworkResult<Token> {
        val token = loadFromStorage()
        with (token) {
            if (this == null) return tokenNotFoundFailure
            if (!isValid()) return invalidTokenFailure
        }

        return Ok(token!!)
    }

    private suspend fun loadFromStorage(): Token? {
        return tokenStorage.load()
    }
}

object FakeAccessTokenRepository: FakeTokenRepository<LoginAccessToken>() {
    override val tokenStorage: TokenStorage<LoginAccessToken> = FakeAccessTokenStorage

    override val tokenNotFoundFailure: TokenNotFound = AccessTokenNotFound
    override val invalidTokenFailure: InvalidToken = InvalidAccessToken
}

object FakeRefreshTokenRepository: FakeTokenRepository<LoginRefreshToken>() {
    override val tokenStorage: TokenStorage<LoginRefreshToken> = FakeRefreshTokenStorage

    override val tokenNotFoundFailure: TokenNotFound = RefreshTokenNotFound
    override val invalidTokenFailure: InvalidToken = InvalidRefreshToken
}