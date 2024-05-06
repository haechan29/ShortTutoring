package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.AuthFailure
import org.softwaremaestro.domain.mylogin.entity.AuthOk
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.TokenIssuer

abstract class FakeTokenIssuer: TokenIssuer {
    override suspend fun issueToken(authFailure: AuthFailure): NetworkResult<Any> {
        return when(authFailure) {
            is AccessTokenIsNotAuthenticated -> tryOrNull(3) { issueAccessToken() }
            is RefreshTokenIsNotAuthenticated -> tryOrNull(3) { issueAccessAndRefreshToken() }
        }
    }

    private suspend fun issueAccessToken(): NetworkResult<Any> {
        return Ok(Unit)
    }

    private suspend fun issueAccessAndRefreshToken(): NetworkResult<Any> {
        return Ok(Unit)
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
}