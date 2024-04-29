package org.softwaremaestro.data.login

interface Server {
    suspend fun send(request: Request)
    suspend fun receive()
}