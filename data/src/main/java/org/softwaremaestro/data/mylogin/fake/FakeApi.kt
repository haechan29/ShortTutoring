package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidAccessToken
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.Server

class FakeApi(
    val tokenRepository: TokenRepository,
    val request: Request,
    val server: Server = object: Server {
        override suspend fun sendRequest(request: Request): NetworkResult<Any> {
            return Ok(Unit)
        }
    }
): Api {
    override suspend fun sendRequest(): NetworkResult<Any> {
        return when (val result = tokenRepository.load()) {
            is Failure -> {
                result
            }
            is Ok -> {
                val token = result.body
                request.addToHeader(token)
                server.sendRequest(request)
            }
        }
    }
}