package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.login.Request
import org.softwaremaestro.data.login.Server
import java.util.Stack

object FakeServer: Server {
    val requests = Stack<Request>()

    override suspend fun send(request: Request) {
        requests.push(request)
    }

    override suspend fun receive() {
    }
}