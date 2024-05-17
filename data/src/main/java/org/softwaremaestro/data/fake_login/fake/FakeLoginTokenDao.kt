package org.softwaremaestro.data.fake_login.fake

import android.util.Log
import org.softwaremaestro.data.fake_login.legacy.AccessTokenStorage
import org.softwaremaestro.data.fake_login.legacy.RefreshTokenStorage
import org.softwaremaestro.domain.fake_login.LoginTokenDao
import org.softwaremaestro.domain.fake_login.result.AccessTokenNotFound
import org.softwaremaestro.domain.fake_login.result.InvalidAccessToken
import org.softwaremaestro.domain.fake_login.result.InvalidRefreshToken
import org.softwaremaestro.domain.fake_login.result.InvalidLoginToken
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.domain.fake_login.result.NotIdentifiedUser
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.domain.fake_login.result.RefreshTokenNotFound
import org.softwaremaestro.domain.fake_login.result.LoginTokenNotFound
import org.softwaremaestro.data.fake_login.legacy.LoginTokenStorage
import org.softwaremaestro.data.fake_login.legacy.UserIdentifier
import org.softwaremaestro.domain.fake_login.AccessTokenDao
import org.softwaremaestro.domain.fake_login.RefreshTokenDao
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.entity.Validatable
import javax.inject.Inject

abstract class FakeLoginTokenDao(
    private val loginTokenStorage: LoginTokenStorage,
    private val userIdentifier: UserIdentifier,
    private val loginTokenNotFound: LoginTokenNotFound,
    private val invalidLoginToken: InvalidLoginToken,
): LoginTokenDao {
    private val notIdentifiedUser = NotIdentifiedUser

    override suspend fun save(token: LoginToken): NetworkResult<Unit> {
        if (!isValid(token)) return invalidLoginToken

        saveToStorage(token)

        return NetworkSuccess(Unit)
    }

    override suspend fun load(): NetworkResult<LoginToken> {
        val token = loadFromStorage() ?: return loginTokenNotFound

        if (!isUserIdentified()) return notIdentifiedUser

        return NetworkSuccess(token)
    }

    private fun isValid(token: Validatable): Boolean {
        return token.isValid()
    }

    private suspend fun saveToStorage(token: LoginToken) {
        return loginTokenStorage.save(token)
    }

    private suspend fun isUserIdentified(): Boolean {
        return userIdentifier.identifyUser()
    }

    private suspend fun loadFromStorage(): LoginToken? {
        return loginTokenStorage.load()
    }
}

class FakeAccessTokenDao @Inject constructor(
    accessTokenStorage: AccessTokenStorage,
    userIdentifier: UserIdentifier
): FakeLoginTokenDao(
    accessTokenStorage, userIdentifier, AccessTokenNotFound, InvalidAccessToken
), AccessTokenDao

class FakeRefreshTokenDao @Inject constructor(
    refreshTokenStorage: RefreshTokenStorage,
    userIdentifier: UserIdentifier
): FakeLoginTokenDao(
    refreshTokenStorage, userIdentifier, RefreshTokenNotFound, InvalidRefreshToken
), RefreshTokenDao