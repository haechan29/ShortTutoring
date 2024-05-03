package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface TokenValidator {
    fun isValid(token: LoginToken): Boolean
}