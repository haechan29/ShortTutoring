package org.softwaremaestro.data.fake_login.fake

import org.softwaremaestro.data.fake_login.dto.IssueAccessTokenRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueRefreshTokenRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueLoginTokenRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueTokenResponseDto
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenRepository
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueLoginTokenApi
import org.softwaremaestro.domain.fake_login.util.attemptUntilSuccess
import org.softwaremaestro.domain.fake_login.util.dtoOrNull
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.InvalidLoginInfo
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.data.fake_login.legacy.IssueLoginTokenRepository
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenRepository
import org.softwaremaestro.domain.fake_login.result.AccessTokenNotFound
import org.softwaremaestro.domain.fake_login.result.RefreshTokenNotFound
import org.softwaremaestro.domain.fake_login.result.LoginTokenNotFound
import org.softwaremaestro.domain.fake_login.util.nullIfSuccess
import javax.inject.Inject

abstract class IssueLoginTokenRepositoryImpl(
    private val tokenNotFound: LoginTokenNotFound,
    private val issueTokenApi: IssueLoginTokenApi,
    private val accessTokenStorageRepository: AccessTokenStorageRepositoryImpl,
    private val refreshTokenStorageRepository: RefreshTokenStorageRepositoryImpl
): IssueLoginTokenRepository {
    final override suspend fun issueToken(): NetworkResult<Unit> {
        val dto = getDtoOrNull() ?: return tokenNotFound

        return attemptUntilSuccess(3, InvalidLoginInfo) {
            val result = sendRequest(dto)

            val dto = result.dtoOrNull() ?: return@attemptUntilSuccess result as NetworkFailure

            val tokens = getTokens(dto).ifEmpty { return@attemptUntilSuccess tokenNotFound }
            for (token in tokens) {
                saveOrFail(token)?.let { failure -> return@attemptUntilSuccess failure }
            }

            NetworkSuccess(Unit)
        }
    }

    protected abstract suspend fun getDtoOrNull(): IssueLoginTokenRequestDto?

    private suspend fun sendRequest(dto: IssueLoginTokenRequestDto): NetworkResult<IssueTokenResponseDto> {
        return issueTokenApi.sendRequest(dto)
    }

    private fun getTokens(dto: IssueTokenResponseDto): List<LoginToken> {
        return with (dto) { listOfNotNull(accessToken, refreshToken) }
    }

    private suspend fun saveOrFail(token: LoginToken): NetworkFailure? {
        return when (token) {
            is LoginAccessToken -> accessTokenStorageRepository.save(token)
            is LoginRefreshToken -> refreshTokenStorageRepository.save(token)
        }.nullIfSuccess()
    }
}

class IssueAccessTokenRepositoryImpl @Inject constructor(
    issueAccessTokenApi: IssueAccessTokenApi,
    accessTokenStorageRepository: AccessTokenStorageRepositoryImpl,
    private val refreshTokenStorageRepository: RefreshTokenStorageRepositoryImpl
): IssueLoginTokenRepositoryImpl(
    AccessTokenNotFound, issueAccessTokenApi, accessTokenStorageRepository, refreshTokenStorageRepository
), IssueAccessTokenRepository {
    override suspend fun getDtoOrNull(): IssueAccessTokenRequestDto? {
        val refreshToken = loadRefreshToken().dtoOrNull() ?: return null
        return IssueAccessTokenRequestDto(refreshToken)
    }

    private suspend fun loadRefreshToken(): NetworkResult<LoginRefreshToken> {
        return refreshTokenStorageRepository.load() as NetworkResult<LoginRefreshToken>
    }
}

class IssueRefreshTokenRepositoryImpl @Inject constructor(
    issueRefreshTokenApi: IssueRefreshTokenApi,
    accessTokenStorageRepository: AccessTokenStorageRepositoryImpl,
    refreshTokenStorageRepository: RefreshTokenStorageRepositoryImpl
): IssueLoginTokenRepositoryImpl(
    RefreshTokenNotFound, issueRefreshTokenApi, accessTokenStorageRepository, refreshTokenStorageRepository
), IssueRefreshTokenRepository {

    override suspend fun getDtoOrNull(): IssueRefreshTokenRequestDto? {
        return getLoginInfo().dtoOrNull()
    }

    private suspend fun getLoginInfo(): NetworkResult<IssueRefreshTokenRequestDto> {
        // TODO: 로그인 과정을 통해 LoginInfo를 받아와야 합니다
        val id = "temporary id"
        val password = "temporary password"
        val dto = IssueRefreshTokenRequestDto(id, password)
        return NetworkSuccess(dto)
    }
}