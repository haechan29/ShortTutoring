package org.softwaremaestro.domain.mylogin.entity

interface Api {
    suspend fun sendRequest(): NetworkResult<Any> {
        return Ok("")
    }
}

interface LoginApi: Api {
    suspend fun login(): NetworkResult<Any>
}

interface AuthTokenApi: Api {
    suspend fun authToken(): NetworkResult<Any>
}