package org.softwaremaestro.domain.mylogin.entity

interface AuthResult
interface AuthOk: AuthResult
sealed interface AuthFailure: AuthResult

object AccessTokenIsAuthenticated: AuthOk
object AccessTokenIsNotAuthenticated: AuthFailure
object RefreshTokenIsNotAuthenticated: AuthFailure