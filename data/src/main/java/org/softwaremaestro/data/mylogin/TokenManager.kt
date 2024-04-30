package org.softwaremaestro.data.mylogin

interface TokenManager {
    suspend fun authAccessToken()
    suspend fun authRefreshToken()
}