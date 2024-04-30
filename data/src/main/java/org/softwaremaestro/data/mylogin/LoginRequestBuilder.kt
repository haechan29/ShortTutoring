package org.softwaremaestro.data.mylogin

interface LoginRequestBuilder {
    fun build(id: String, password: String): LoginRequest
}