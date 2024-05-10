package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.IssueTokenApi
import org.softwaremaestro.domain.mylogin.entity.IssueTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenIssuer
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

abstract class FakeTokenIssuer<Token: LoginToken>(
    private val api: IssueTokenApi,
    private val tokenNotFound: TokenNotFound
): TokenIssuer {
    override suspend fun issueToken(): NetworkResult<EmptyResponseDto> {
        val dto = getLoginRequestDto()
        var result = sendRequest(dto)

        if (result is InvalidLoginInfo) return result

        return attemptUntil(3) {
            result = sendRequest(dto)
            if (result is Failure) return@attemptUntil result as Failure

            val body = (result as Ok<ResponseDto>).dto
            val tokens = getTokens(body).ifEmpty { return@attemptUntil tokenNotFound }

            tokens.forEach { token ->
                saveToken(token)
            }

            Ok(EmptyResponseDto)
        }
    }

    protected abstract suspend fun getLoginRequestDto(): LoginRequestDto
    protected abstract suspend fun sendRequest(dto: LoginRequestDto): NetworkResult<IssueTokenResponseDto>
    protected abstract fun getTokens(body: ResponseDto): List<Token>
    protected abstract suspend fun saveToken(token: Token)

    private suspend fun attemptUntil(attemptLimit: Int, f: suspend () -> NetworkResult<EmptyResponseDto>)
    : NetworkResult<EmptyResponseDto> {
        var attempt = 0
        var result = f()
        while (attempt < attemptLimit && result is Failure) {
            result = f()
            attempt++
        }
        return result
    }
}

abstract class FakeAccessTokenIssuer(api: IssueTokenApi, tokenNotFound: AccessTokenNotFound)
    : FakeTokenIssuer<LoginAccessToken>(api, tokenNotFound)

abstract class FakeRefreshTokenIssuer(
    api: IssueTokenApi,
    tokenNotFound: RefreshTokenNotFound
): FakeTokenIssuer<LoginRefreshToken>(api, tokenNotFound) {
    protected abstract fun hasAccessToken(body: Any): Boolean
    protected abstract fun hasRefreshToken(body: Any): Boolean
}