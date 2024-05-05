package org.softwaremaestro.domain.mylogin.entity

interface TotalTokenAuthenticator {
    suspend fun authToken(): AuthResult
}