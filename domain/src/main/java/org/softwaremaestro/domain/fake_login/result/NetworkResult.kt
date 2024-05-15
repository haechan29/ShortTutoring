package org.softwaremaestro.domain.fake_login.result

import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.ACCESS_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.DTO_CONTAINS_NULL_FIELD
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.INVALID_ACCESS_TOKEN
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.INVALID_LOGIN_INFO
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.INVALID_REFRESH_TOKEN
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.NOT_IDENTIFIED_USER
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.REFRESH_TOKEN_NOT_FOUND

interface Network<out T>

sealed interface NetworkResult<out T>: Result<Network<T>>

data class NetworkSuccess<out T>(val dto: T): NetworkResult<T>, Success<Network<T>>
interface NetworkFailure: NetworkResult<Nothing>, Failure<Network<Nothing>>

object InvalidLoginInfo: NetworkFailure { override val message = INVALID_LOGIN_INFO }
object NotIdentifiedUser: NetworkFailure { override val message = NOT_IDENTIFIED_USER }
object DtoContainsNullFieldFailure: NetworkFailure { override val message = DTO_CONTAINS_NULL_FIELD }

sealed interface LoginTokenNotFound: NetworkFailure
object AccessTokenNotFound  : LoginTokenNotFound { override val message = ACCESS_TOKEN_NOT_FOUND }
object RefreshTokenNotFound : LoginTokenNotFound { override val message = REFRESH_TOKEN_NOT_FOUND }

sealed interface InvalidLoginToken: NetworkFailure
object InvalidAccessToken   : InvalidLoginToken { override val message = INVALID_ACCESS_TOKEN }
object InvalidRefreshToken  : InvalidLoginToken { override val message = INVALID_REFRESH_TOKEN }