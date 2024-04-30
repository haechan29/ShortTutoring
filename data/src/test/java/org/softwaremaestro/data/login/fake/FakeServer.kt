package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.mylogin.Request
import org.softwaremaestro.data.mylogin.Server
import java.util.Stack

object FakeServer: Server {
    val requests = Stack<Request>()

    override suspend fun send(request: Request) {
        requests.push(request)
    }

    override suspend fun receive() {
    }
}