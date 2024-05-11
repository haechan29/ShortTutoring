package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.ACCESS_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_ACCESS_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_LOGIN_INFO
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_REFRESH_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.NOT_IDENTIFIED_USER
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.REFRESH_TOKEN_NOT_FOUND

interface Network<out Dto: ResponseDto>

sealed class NetworkResult<out Dto: ResponseDto>: Result<Network<Dto>>
data class NetworkSuccess<out Dto: ResponseDto>(val dto: Dto): NetworkResult<Dto>(), Success<Network<Dto>>
abstract class NetworkFailure: NetworkResult<Nothing>(), Failure<Network<Nothing>>

object InvalidLoginInfo: NetworkFailure() { override val message = INVALID_LOGIN_INFO }
object NotIdentifiedUser: NetworkFailure() { override val message = NOT_IDENTIFIED_USER }

sealed class TokenNotFound  : NetworkFailure()
object AccessTokenNotFound  : TokenNotFound() { override val message = ACCESS_TOKEN_NOT_FOUND }
object RefreshTokenNotFound : TokenNotFound() { override val message = REFRESH_TOKEN_NOT_FOUND }

sealed class InvalidToken   : NetworkFailure()
object InvalidAccessToken   : InvalidToken() { override val message = INVALID_ACCESS_TOKEN }
object InvalidRefreshToken  : InvalidToken() { override val message = INVALID_REFRESH_TOKEN }