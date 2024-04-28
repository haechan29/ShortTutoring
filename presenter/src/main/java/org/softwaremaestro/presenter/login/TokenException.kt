package org.softwaremaestro.presenter.login

class TokenException: Exception() {
    override val message = "Token is invalid. Login failed."
}
