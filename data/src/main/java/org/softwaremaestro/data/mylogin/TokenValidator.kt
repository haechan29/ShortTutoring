package org.softwaremaestro.data.mylogin

import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface TokenValidator {
    fun isValid(token: LoginToken): Boolean
}