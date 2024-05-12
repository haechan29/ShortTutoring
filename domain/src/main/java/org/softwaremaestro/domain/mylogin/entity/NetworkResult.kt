package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.ACCESS_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_ACCESS_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_LOGIN_INFO
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_REFRESH_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.NOT_IDENTIFIED_USER
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.REFRESH_TOKEN_NOT_FOUND

interface Network<out Dto: ResponseDto>

sealed interface NetworkResult<out Dto: ResponseDto>: Result<Network<Dto>>

data class NetworkSuccess<out Dto: ResponseDto>(val dto: Dto): NetworkResult<Dto>, Success<Network<Dto>>
interface NetworkFailure: NetworkResult<Nothing>, Failure<Network<Nothing>>

object InvalidLoginInfo: NetworkFailure { override val message = INVALID_LOGIN_INFO }
object NotIdentifiedUser: NetworkFailure { override val message = NOT_IDENTIFIED_USER }

sealed interface TokenNotFound<Token: LoginToken>: NetworkFailure
object AccessTokenNotFound  : TokenNotFound<LoginAccessToken>  { override val message = ACCESS_TOKEN_NOT_FOUND }
object RefreshTokenNotFound : TokenNotFound<LoginRefreshToken> { override val message = REFRESH_TOKEN_NOT_FOUND }

sealed interface InvalidToken<Token: LoginToken>: NetworkFailure
object InvalidAccessToken   : InvalidToken<LoginAccessToken>  { override val message = INVALID_ACCESS_TOKEN }
object InvalidRefreshToken  : InvalidToken<LoginRefreshToken> { override val message = INVALID_REFRESH_TOKEN }