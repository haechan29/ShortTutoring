package org.softwaremaestro.data.login

interface LoginValidator {
    fun validate(id: String, password: String)
}