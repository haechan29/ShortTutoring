package org.softwaremaestro.domain.login.entity.exception

object InvalidIdException: Exception() {
    override val message = "Id is invalid."
}