package org.softwaremaestro.domain.mylogin.entity

sealed class NetworkResult<out T>
abstract class Failure: NetworkResult<Nothing>() {
    abstract val message: String

    companion object {
        const val INVALID_LOGIN_INFO      = "로그인 정보가 유효하지 않습니다"
        const val ACCESS_TOKEN_NOT_FOUND  = "액세스 토큰이 존재하지 않습니다"
        const val INVALID_ACCESS_TOKEN    = "액세스 토큰이 유효하지 않습니다"
        const val REFRESH_TOKEN_NOT_FOUND = "리프레시 토큰이 존재하지 않습니다"
        const val INVALID_REFRESH_TOKEN   = "리프레시 토큰이 유효하지 않습니다"
    }
}

object InvalidLoginInfo: Failure() { override val message = INVALID_LOGIN_INFO }

sealed class TokenNotFound  : Failure()
object AccessTokenNotFound  : TokenNotFound() { override val message = ACCESS_TOKEN_NOT_FOUND }
object RefreshTokenNotFound : TokenNotFound() { override val message = REFRESH_TOKEN_NOT_FOUND }

sealed class InvalidToken   : Failure()
object InvalidAccessToken   : InvalidToken() { override val message = INVALID_ACCESS_TOKEN }
object InvalidRefreshToken  : InvalidToken() { override val message = INVALID_REFRESH_TOKEN }

data class Ok<T>(val body: T): NetworkResult<T>()