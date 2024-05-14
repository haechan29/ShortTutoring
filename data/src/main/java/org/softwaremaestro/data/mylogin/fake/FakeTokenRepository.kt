package org.softwaremaestro.data.mylogin.fake

import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.result.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.InvalidAccessToken
import org.softwaremaestro.domain.mylogin.entity.result.InvalidRefreshToken
import org.softwaremaestro.domain.mylogin.entity.result.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.dto.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.result.NotIdentifiedUser
import org.softwaremaestro.domain.mylogin.entity.result.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.result.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.result.TokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenStorage
import org.softwaremaestro.domain.mylogin.entity.UserIdentifier
import org.softwaremaestro.domain.mylogin.entity.Validatable

abstract class FakeTokenRepository<Token: LoginToken>(
    private val tokenStorage: TokenStorage<Token>,
    private val userIdentifier: UserIdentifier,
    private val tokenNotFoundFailure: TokenNotFound<Token>,
    private val invalidTokenFailure: InvalidToken<Token>
): TokenRepository<Token> {
    private val notIdentifiedUser = NotIdentifiedUser

    override suspend fun save(token: Token): NetworkResult<EmptyResponseDto> {
        if (!isValid(token)) return invalidTokenFailure

        saveToStorage(token)

        return NetworkSuccess(EmptyResponseDto)
    }

    override suspend fun load(): NetworkResult<LocalTokenResponseDto> {
        val token = loadFromStorage() ?: return tokenNotFoundFailure
        if (!isUserIdentified()) return notIdentifiedUser
        val dto = toDto(token)
        return NetworkSuccess(dto)
    }

    private fun isValid(token: Validatable): Boolean {
        return token.isValid()
    }

    private suspend fun saveToStorage(token: Token) {
        return tokenStorage.save(token)
    }

    private suspend fun isUserIdentified(): Boolean {
        return userIdentifier.identifyUser()
    }

    private suspend fun loadFromStorage(): Token? {
        return tokenStorage.load()
    }

    private fun toDto(token: Token): LocalTokenResponseDto {
        TODO()
    }
}

abstract class FakeAccessTokenRepository(
    tokenStorage: TokenStorage<LoginAccessToken> = FakeAccessTokenStorage,
    userIdentifier: UserIdentifier
): FakeTokenRepository<LoginAccessToken>(tokenStorage, userIdentifier, AccessTokenNotFound, InvalidAccessToken)

abstract class FakeRefreshTokenRepository(
    tokenStorage: TokenStorage<LoginRefreshToken> = FakeRefreshTokenStorage,
    userIdentifier: UserIdentifier
): FakeTokenRepository<LoginRefreshToken>(tokenStorage, userIdentifier, RefreshTokenNotFound, InvalidRefreshToken)