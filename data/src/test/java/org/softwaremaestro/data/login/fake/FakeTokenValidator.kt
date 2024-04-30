package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.mylogin.TokenValidator
import org.softwaremaestro.domain.mylogin.exception.InvalidTokenException
import org.softwaremaestro.domain.mylogin.entity.LoginToken

object FakeTokenValidator: TokenValidator {
    override fun validate(token: LoginToken) {
        if (!token.isValid()) {
            throw InvalidTokenException
        }
    }
}