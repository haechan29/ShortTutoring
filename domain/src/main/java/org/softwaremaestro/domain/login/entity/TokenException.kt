package org.softwaremaestro.domain.login.entity

sealed class TokenException: Exception() {
    override val message = "Token is invalid. Login failed."
}

object TokenNotFoundException: TokenException()
object InvalidTokenException:  TokenException()
