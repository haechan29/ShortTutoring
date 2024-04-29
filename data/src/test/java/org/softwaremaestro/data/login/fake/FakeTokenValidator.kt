package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.login.TokenValidator
import org.softwaremaestro.domain.login.entity.InvalidTokenException
import org.softwaremaestro.domain.login.entity.LoginToken

object FakeTokenValidator: TokenValidator {
    override fun validate(token: LoginToken) {
        if (!token.isValid()) {
            throw InvalidTokenException
        }
    }
}