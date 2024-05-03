package org.softwaremaestro.domain.mylogin.entity

sealed class LoginToken(val content: String): Validatable
open class LoginAccessToken(content: String): LoginToken(content) {
    override fun isValid(): Boolean {
        return false
    }
}

open class LoginRefreshToken(content: String): LoginToken(content) {
    override fun isValid(): Boolean {
        return false
    }
}