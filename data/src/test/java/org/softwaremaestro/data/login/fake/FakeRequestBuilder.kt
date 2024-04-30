package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.mylogin.LoginRequest
import org.softwaremaestro.data.mylogin.LoginRequestBuilder
import org.softwaremaestro.data.mylogin.LoginValidator
import org.softwaremaestro.domain.mylogin.entity.exception.InvalidIdException
import org.softwaremaestro.domain.mylogin.entity.exception.InvalidPasswordException

object FakeRequestBuilder: LoginRequestBuilder {
    override fun build(id: String, password: String): LoginRequest {
        FakeLoginValidator.validate(id, password)

        return FakeLoginRequest(id, password)
    }
}

private data class FakeLoginRequest(val id: String, val password: String): LoginRequest

private object FakeLoginValidator: LoginValidator {
    override fun validate(id: String, password: String) {
        if (id.isEmpty()) {
            throw InvalidIdException
        }

        if (password.isEmpty()) {
            throw InvalidPasswordException
        }
    }
}