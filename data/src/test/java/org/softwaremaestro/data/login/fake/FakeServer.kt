package org.softwaremaestro.data.login.fake

import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.Server
import java.util.Stack

object FakeServer: Server {
    val requests = Stack<Request>()

    override suspend fun send(request: Request) {
        requests.push(request)
    }

    override suspend fun receive() {
    }
}