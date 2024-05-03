package org.softwaremaestro.domain.mylogin.entity

interface TokenManager {
    suspend fun authAccessToken()
    suspend fun authRefreshToken()
}