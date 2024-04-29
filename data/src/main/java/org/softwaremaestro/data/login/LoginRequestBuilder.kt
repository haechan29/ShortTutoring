package org.softwaremaestro.data.login

interface LoginRequestBuilder {
    fun build(id: String, password: String): LoginRequest
}