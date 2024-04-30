package org.softwaremaestro.data.mylogin

interface Server {
    suspend fun send(request: Request)
    suspend fun receive()
}