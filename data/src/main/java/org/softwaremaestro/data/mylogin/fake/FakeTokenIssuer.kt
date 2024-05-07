package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.dto.EmptyRequestDto
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AuthFailure
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.IssueTokenApi
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.RequestDto
import org.softwaremaestro.domain.mylogin.entity.TokenIssuer

object FakeTokenIssuer: TokenIssuer {
    private val issueAccessTokenApi: IssueTokenApi = FakeIssueAccessTokenApi
    private val issueRefreshTokenApi: IssueTokenApi = FakeIssueRefreshTokenApi

    override suspend fun issueToken(authFailure: AuthFailure): NetworkResult<Any> {
        return when(authFailure) {
            is AccessTokenIsNotAuthenticated -> tryOrNull(3) { issueAccessToken() }
            is RefreshTokenIsNotAuthenticated -> {
                val loginRequestDto = requestLogin()
                tryOrNull(3) { issueAccessAndRefreshToken(loginRequestDto) }
            }
        }
    }

    private suspend fun issueAccessToken(): NetworkResult<Any> {
        return issueAccessTokenApi.sendRequest(EmptyRequestDto)
    }

    private suspend fun issueAccessAndRefreshToken(loginRequestDto: LoginRequestDto): NetworkResult<Any> {
        return issueRefreshTokenApi.sendRequest(loginRequestDto)
    }

    private suspend fun tryOrNull(attemptLimit: Int, f: suspend () -> NetworkResult<Any>): NetworkResult<Any> {
        var attempt = 0
        var result = f()
        while (attempt < attemptLimit && result is Failure) {
            result = f()
            attempt++
        }
        return result
    }

    fun requestLogin(): LoginRequestDto {
        TODO()
    }
}