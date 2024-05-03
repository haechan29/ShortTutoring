package org.softwaremaestro.data.login.fake

import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.LoginToken

object FakeTokenValidator: TokenValidator {
    override fun isValid(token: LoginToken): Boolean {
        return token.isValid()
    }
}