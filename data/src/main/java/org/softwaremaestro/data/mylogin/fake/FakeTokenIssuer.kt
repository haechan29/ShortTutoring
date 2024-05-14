package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.data.mylogin.util.attemptUntilSuccess
import org.softwaremaestro.data.mylogin.util.dtoOrNull
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.result.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.result.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.result.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.result.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenIssuer
import org.softwaremaestro.domain.mylogin.entity.result.TokenNotFound

abstract class FakeTokenIssuer<Token: LoginToken>(
    private val tokenNotFound: TokenNotFound<Token>
): TokenIssuer<Token> {
    final override suspend fun issueToken(): NetworkResult<EmptyResponseDto> {
        val dto = getDtoOrNull() ?: return tokenNotFound

        return attemptUntilSuccess(3, InvalidLoginInfo) {
            val result = sendRequest(dto)

            val body = result.dtoOrNull() ?: return@attemptUntilSuccess result as NetworkFailure

            val tokens = getTokens(body).ifEmpty { return@attemptUntilSuccess tokenNotFound }

            tokens.forEach { token ->
                saveTokenToStorage(token)
            }

            NetworkSuccess(EmptyResponseDto)
        }
    }

    private suspend fun saveToken() {
        TODO()
    }

    private suspend fun getDtoOrNull(): LoginRequestDto? {
        return when(val dtoResult = getLocalTokenDtoResult()) {
            is NetworkFailure -> return null
            is NetworkSuccess -> toDto(dtoResult.dto)
        }
    }

    protected abstract suspend fun getLocalTokenDtoResult(): NetworkResult<LocalTokenResponseDto>

    private fun toDto(dto: LocalTokenResponseDto): LoginRequestDto {
        TODO()
    }

    private suspend fun sendRequest(dto: LoginRequestDto): NetworkResult<LoginResponseDto> {
        TODO()
    }

    protected abstract fun getTokens(body: ResponseDto): List<Token>

    private suspend fun saveTokenToStorage(token: Token) {
        TODO()
    }
}

abstract class FakeAccessTokenIssuer(
    private val tokenRepository: TokenRepository<LoginAccessToken>,
): FakeTokenIssuer<LoginAccessToken>(AccessTokenNotFound) {
    override suspend fun getLocalTokenDtoResult(): NetworkResult<LocalTokenResponseDto> {
        return loadRefreshToken()
    }

    private suspend fun loadRefreshToken(): NetworkResult<LocalTokenResponseDto> {
        return tokenRepository.load()
    }
}

abstract class FakeRefreshTokenIssuer(
    tokenNotFound: RefreshTokenNotFound = RefreshTokenNotFound
): FakeTokenIssuer<LoginRefreshToken>(tokenNotFound) {
    final override suspend fun getLocalTokenDtoResult(): NetworkResult<LocalTokenResponseDto> {
        return getLoginInfo()
    }

    private suspend fun getLoginInfo(): NetworkResult<LocalTokenResponseDto> {
        TODO("로그인")
    }
}