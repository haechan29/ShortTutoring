package org.softwaremaestro.data.mylogin

import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface TokenValidator {
    fun validate(token: LoginToken)
}