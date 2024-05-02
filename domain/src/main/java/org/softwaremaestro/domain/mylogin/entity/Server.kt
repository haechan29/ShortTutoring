package org.softwaremaestro.domain.mylogin.entity

interface Server {
    suspend fun send(request: Request)
    suspend fun receive()
}