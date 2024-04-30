package org.softwaremaestro.domain.mylogin.entity

data class LoginToken(val content: String, var isValid: () -> Boolean)
