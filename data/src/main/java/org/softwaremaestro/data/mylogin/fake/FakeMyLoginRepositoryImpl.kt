package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.MyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.AttemptResult
import org.softwaremaestro.domain.mylogin.entity.Ok

class FakeMyLoginRepositoryImpl(
    private val api: Api,
    private val tokenRepository: TokenRepository<String>
): MyLoginRepository<String> {
    override suspend fun login(id: String, password: String): AttemptResult<String> {
        val dto = LoginRequestDto(id, password)
        if (!dto.isValid()) {
            return AccessTokenNotFound
        }

        api.send(dto)

        return Ok("result")
    }

    override suspend fun autologin() {
        tokenRepository.authAccessToken()
    }
}