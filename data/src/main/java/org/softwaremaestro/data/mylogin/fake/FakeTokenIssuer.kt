package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.IssueTokenApi
import org.softwaremaestro.domain.mylogin.entity.IssueTokenRequestDto
import org.softwaremaestro.domain.mylogin.entity.IssueTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenIssuer
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

abstract class FakeTokenIssuer<Token: LoginToken>(
    private val api: IssueTokenApi,
    private val tokenNotFound: TokenNotFound<Token>
): TokenIssuer {
    final override suspend fun issueToken(): NetworkResult<EmptyResponseDto> {
        val dto = getDtoOrNull() ?: return tokenNotFound

        var result = sendRequest(dto)

        if (result is InvalidLoginInfo) return result

        return attemptUntil(3) {
            result = sendRequest(dto)
            if (result is NetworkFailure) return@attemptUntil result as NetworkFailure

            val body = (result as NetworkSuccess<ResponseDto>).dto
            val tokens = getTokens(body).ifEmpty { return@attemptUntil tokenNotFound }

            tokens.forEach { token ->
                saveToken(token)
            }

            NetworkSuccess(EmptyResponseDto)
        }
    }

    private suspend fun getDtoOrNull(): IssueTokenRequestDto? {
        return when(val dtoResult = getDtoResult()) {
            is NetworkFailure -> return null
            is NetworkSuccess -> toDto(dtoResult.dto)
        }
    }

    protected abstract suspend fun getDtoResult(): NetworkResult<LocalTokenResponseDto>

    private fun toDto(dto: LocalTokenResponseDto): IssueTokenRequestDto {
        TODO()
    }

    private suspend fun sendRequest(dto: IssueTokenRequestDto): NetworkResult<IssueTokenResponseDto> {
        TODO()
    }

    protected abstract fun getTokens(body: ResponseDto): List<Token>

    private suspend fun saveToken(token: Token) {
        TODO()
    }

    private suspend fun attemptUntil(attemptLimit: Int, f: suspend () -> NetworkResult<EmptyResponseDto>)
    : NetworkResult<EmptyResponseDto> {
        var attempt = 0
        var result = f()
        while (attempt < attemptLimit && result is NetworkFailure) {
            result = f()
            attempt++
        }
        return result
    }
}

abstract class FakeAccessTokenIssuer(
    private val tokenRepository: TokenRepository<LoginAccessToken>,
    api: IssueTokenApi,
    tokenNotFound: AccessTokenNotFound = AccessTokenNotFound
): FakeTokenIssuer<LoginAccessToken>(api, tokenNotFound) {
    override suspend fun getDtoResult(): NetworkResult<LocalTokenResponseDto> {
        return loadRefreshToken()
    }

    private suspend fun loadRefreshToken(): NetworkResult<LocalTokenResponseDto> {
        return tokenRepository.load()
    }
}

abstract class FakeRefreshTokenIssuer(
    api: IssueTokenApi,
    tokenNotFound: RefreshTokenNotFound = RefreshTokenNotFound
): FakeTokenIssuer<LoginRefreshToken>(api, tokenNotFound) {
    final override suspend fun getDtoResult(): NetworkResult<LocalTokenResponseDto> {
        return getLoginInfo()
    }

    private suspend fun getLoginInfo(): NetworkResult<LocalTokenResponseDto> {
        TODO()
    }
}