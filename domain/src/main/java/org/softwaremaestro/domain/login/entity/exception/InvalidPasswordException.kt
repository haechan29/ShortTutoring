package org.softwaremaestro.domain.login.entity.exception

object InvalidPasswordException: Exception() {
    override val message = "Password is invalid."
}