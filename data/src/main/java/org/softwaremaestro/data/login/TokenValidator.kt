package org.softwaremaestro.data.login

import org.softwaremaestro.domain.login.entity.InvalidTokenException
import org.softwaremaestro.domain.login.entity.LoginToken

interface TokenValidator {
    fun validate(token: LoginToken)
}

object FakeTokenValidator: TokenValidator {
    override fun validate(token: LoginToken) {
        if (!token.isValid()) {
            throw InvalidTokenException
        }
    }
}