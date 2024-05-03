package org.softwaremaestro.domain.mylogin.entity

sealed class AttemptResult<out T>
abstract class Failure(val message: String): AttemptResult<Nothing>() {
    companion object {
        const val ACCESS_TOKEN_NOT_FOUND  = "액세스 토큰이 존재하지 않습니다"
        const val INVALID_ACCESS_TOKEN    = "액세스 토큰이 유효하지 않습니다"
        const val REFRESH_TOKEN_NOT_FOUND = "리프레시 토큰이 존재하지 않습니다"
        const val INVALID_REFRESH_TOKEN   = "리프레시 토큰이 유효하지 않습니다"
    }
}
object AccessTokenNotFound : Failure(ACCESS_TOKEN_NOT_FOUND)
object InvalidAccessToken  : Failure(INVALID_ACCESS_TOKEN)
object RefreshTokenNotFound: Failure(REFRESH_TOKEN_NOT_FOUND)
object InvalidRefreshToken : Failure(INVALID_REFRESH_TOKEN)

data class Ok<T>(val body: T): AttemptResult<T>()