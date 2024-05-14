package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.result.AuthResult

interface TokenAuthenticator {
    suspend fun authToken(): AuthResult
}