package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.RequestDto
import org.softwaremaestro.domain.mylogin.entity.Server
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

abstract class FakeMyLoginRepository: MyLoginRepository {
    abstract val api: LoginApi

    private val accessTokenRepository: TokenRepository<LoginAccessToken> = FakeAccessTokenRepository
    private val refreshTokenRepository: TokenRepository<LoginRefreshToken> = FakeRefreshTokenRepository

    private val tokenNotFound = InvalidLoginInfo

    override suspend fun login(id: String, password: String): NetworkResult<Unit> {
        val dto = LoginRequestDto(id, password)
        if (!dto.isValid()) {
            return tokenNotFound
        }

        val result = api.login()
        if (result is Ok<LoginToken>) {
            saveToken(result)
        }

        return when (result) {
            is Ok -> Ok(Unit)
            is Failure -> result
        }
    }

    override suspend fun autologin(): NetworkResult<Unit> {
        return when (val result = loadAccessToken()) {
            is Ok -> Ok(Unit)
            is Failure -> {
                loadRefreshToken()
                result
            }
        }
    }

    private suspend fun saveToken(result: Ok<LoginToken>) {
        when (val body = result.body) {
            is LoginAccessToken -> saveAccessToken(body)
            is LoginRefreshToken -> saveRefreshToken(body)
        }
    }

    private suspend fun saveAccessToken(token: LoginAccessToken): NetworkResult<Unit> {
        return accessTokenRepository.save(token)
    }

    private suspend fun saveRefreshToken(token: LoginRefreshToken): NetworkResult<Unit> {
        return refreshTokenRepository.save(token)
    }

    private suspend fun loadAccessToken(): NetworkResult<LoginToken> {
        return accessTokenRepository.load()
    }

    private suspend fun loadRefreshToken(): NetworkResult<LoginToken> {
        return refreshTokenRepository.load()
    }
}