package org.softwaremaestro.domain.mylogin.entity

sealed class LoginToken(val content: String): Validatable
class LoginAccessToken(content: String): LoginToken(content) {
    override fun isValid(): Boolean {
        return false
    }
}

class LoginRefreshToken(content: String): LoginToken(content) {
    override fun isValid(): Boolean {
        return false
    }
}