package org.softwaremaestro.domain.mylogin.entity.exception

sealed class TokenException: Exception()
object TokenNotFoundException: TokenException()