package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.ACCESS_TOKEN_NOT_AUTHENTICATED
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.REFRESH_TOKEN_NOT_AUTHENTICATED

interface Authentication

object AccessTokenIsAuthenticated: Success<Authentication>
object AccessTokenIsNotAuthenticated: Failure<Authentication> { override val message = ACCESS_TOKEN_NOT_AUTHENTICATED }
object RefreshTokenIsNotAuthenticated: Failure<Authentication> { override val message = REFRESH_TOKEN_NOT_AUTHENTICATED }