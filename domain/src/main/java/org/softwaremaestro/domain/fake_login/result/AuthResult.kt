package org.softwaremaestro.domain.fake_login.result

import org.softwaremaestro.domain.fake_login.result.Failure.Companion.ACCESS_TOKEN_NOT_AUTHENTICATED
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.REFRESH_TOKEN_NOT_AUTHENTICATED

interface Authentication

sealed interface AuthResult: Result<Authentication>

interface AuthSuccess           : AuthResult, Success<Authentication>
sealed interface AuthFailure    : AuthResult, Failure<Authentication>

object AccessTokenIsAuthenticated       : AuthSuccess
object AccessTokenIsNotAuthenticated    : AuthFailure { override val message = ACCESS_TOKEN_NOT_AUTHENTICATED }
object RefreshTokenIsNotAuthenticated   : AuthFailure { override val message = REFRESH_TOKEN_NOT_AUTHENTICATED }