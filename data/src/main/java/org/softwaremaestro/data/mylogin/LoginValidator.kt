package org.softwaremaestro.data.mylogin

interface LoginValidator {
    fun validate(id: String, password: String)
}