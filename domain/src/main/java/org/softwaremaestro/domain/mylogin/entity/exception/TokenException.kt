package org.softwaremaestro.domain.mylogin.exception

sealed class TokenException: Exception()

object TokenNotFoundException: TokenException()
object InvalidTokenException: TokenException()

object InvalidAccessTokenException: Exception() {
    override val message = "Access Token is invalid."
}