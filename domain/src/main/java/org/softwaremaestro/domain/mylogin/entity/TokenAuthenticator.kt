package org.softwaremaestro.domain.mylogin.entity

interface TokenAuthenticator {
    suspend fun authToken(): AuthResult
}