package org.softwaremaestro.domain.mylogin.entity

interface Api {
    val server: Server

    suspend fun sendRequest(dto: RequestDto): NetworkResult<Any> {
        val request = toRequest(dto)
        addTokenToRequestHeader()
        return sendToServer(request)
    }

    fun toRequest(dto: RequestDto): Request

    suspend fun addTokenToRequestHeader(): NetworkResult<Any> {
        return Ok(Unit)
    }

    suspend fun sendToServer(request: Request): NetworkResult<Any> {
        return server.send(request)
    }
}

interface LoginApi: Api {
    suspend fun login(): NetworkResult<LoginToken>
}

interface IssueTokenApi: Api