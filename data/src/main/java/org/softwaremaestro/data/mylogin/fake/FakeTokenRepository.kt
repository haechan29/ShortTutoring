package org.softwaremaestro.data.mylogin.fake

import kotlinx.coroutines.delay
import org.softwaremaestro.data.mylogin.dto.EmptyRequestDto
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.AttemptResult
import org.softwaremaestro.domain.mylogin.entity.InvalidAccessToken
import org.softwaremaestro.domain.mylogin.entity.InvalidRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

class FakeTokenRepository(
    private val tokenStorage: TokenStorage
): TokenRepository<String> {
    override suspend fun authAccessToken(): AttemptResult<String> {
        with (readAccessToken()) {
            if (this == null) return AccessTokenNotFound
            if (!this.isValid()) return InvalidAccessToken
        }

        return Ok("")
    }

    private suspend fun readAccessToken(): LoginAccessToken? {
        delay(100)
        return null
    }

    override suspend fun authRefreshToken(): AttemptResult<String> {
        with (readRefreshToken()) {
            if (this == null) return RefreshTokenNotFound
            if (!this.isValid()) return InvalidRefreshToken
        }

        return Ok("")
    }

    private suspend fun readRefreshToken(): LoginRefreshToken? {
        delay(100)
        return null
    }

    override suspend fun save(token: LoginToken) {
        if (token.isValid()) {
            tokenStorage.save(token)
        }
    }

    override suspend fun load(): LoginToken? {
        val token = tokenStorage.load() ?: return null

        return if (token.isValid()) token else null
    }
}