package org.softwaremaestro.domain.mylogin.entity

interface Server {
    suspend fun sendRequest(request: Request): NetworkResult<Any>
}