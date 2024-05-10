package org.softwaremaestro.domain.mylogin.entity

sealed interface AuthResult: Result
interface AuthOk: AuthResult, Ok
sealed interface AuthFailure: AuthResult, Failure

object AccessTokenIsAuthenticated: AuthOk
object AccessTokenIsNotAuthenticated: AuthFailure
object RefreshTokenIsNotAuthenticated: AuthFailure