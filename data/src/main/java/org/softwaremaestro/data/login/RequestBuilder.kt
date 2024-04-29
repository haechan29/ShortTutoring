package org.softwaremaestro.data.login

import org.softwaremaestro.data.login.Request

interface RequestBuilder {
    fun create(): Request
}