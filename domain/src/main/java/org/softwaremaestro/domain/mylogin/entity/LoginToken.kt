package org.softwaremaestro.domain.mylogin.entity

sealed class LoginToken(val content: String, var isValid: () -> Boolean)
open class AccessLoginToken(content: String, isValid: () -> Boolean): LoginToken(content, isValid)
open class RefreshLoginToken(content: String, isValid: () -> Boolean): LoginToken(content, isValid)