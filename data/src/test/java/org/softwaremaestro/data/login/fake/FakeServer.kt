package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.login.Server

object FakeServer: Server {
    override suspend fun sendRequest() {
    }

    override suspend fun receiveResponse() {
        TODO("Not yet implemented")
    }
}