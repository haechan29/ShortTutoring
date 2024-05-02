package org.softwaremaestro.data.mylogin

import org.softwaremaestro.domain.mylogin.entity.LoginRequest

interface LoginRequestBuilder {
    fun build(id: String, password: String): LoginRequest
}