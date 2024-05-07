package org.softwaremaestro.domain.mylogin.entity

interface Token: Validatable

sealed interface LoginToken: Token
class LoginAccessToken(content: String): LoginToken {
    override fun isValid(): Boolean {
        return false
    }
}

class LoginRefreshToken(content: String): LoginToken {
    override fun isValid(): Boolean {
        return false
    }
}