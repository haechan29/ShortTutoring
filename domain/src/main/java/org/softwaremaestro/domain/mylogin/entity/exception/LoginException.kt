package org.softwaremaestro.domain.mylogin.entity.exception

sealed class LoginException: Exception()
object InvalidIdException: LoginException() {
    override val message = "Id is invalid."
}

object InvalidPasswordException: LoginException() {
    override val message = "Password is invalid."
}