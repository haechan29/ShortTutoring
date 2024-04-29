package org.softwaremaestro.data.login

interface Server {
    suspend fun sendRequest()
    suspend fun receiveResponse()
}