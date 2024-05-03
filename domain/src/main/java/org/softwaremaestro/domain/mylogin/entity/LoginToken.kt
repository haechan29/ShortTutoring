package org.softwaremaestro.domain.mylogin.entity

sealed class LoginToken(val content: String, var isValid: () -> Boolean)
open class LoginAccessToken(content: String, isValid: () -> Boolean): LoginToken(content, isValid)
open class LoginRefreshToken(content: String, isValid: () -> Boolean): LoginToken(content, isValid)