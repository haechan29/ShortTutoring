package org.softwaremaestro.data.fake_login.legacy

import org.softwaremaestro.domain.fake_login.result.AuthResult

interface LoginTokenAuthenticator {
    suspend fun authLoginToken(): AuthResult
}