package org.softwaremaestro.domain.fake_login.result

sealed interface Result<out Field>
interface Success<out Field>: Result<Field>
interface Failure<out Field>: Result<Field> {
    val message: String

    companion object {
        const val INVALID_LOGIN_INFO              = "로그인 정보가 유효하지 않습니다"
        const val NOT_IDENTIFIED_USER             = "식별되지 않은 사용자입니다"
        const val DTO_CONTAINS_NULL_FIELD         = "DTO가 null 필드를 포함합니다"

        const val ACCESS_TOKEN_NOT_FOUND          = "액세스 토큰이 존재하지 않습니다"
        const val INVALID_ACCESS_TOKEN            = "액세스 토큰이 유효하지 않습니다"
        const val REFRESH_TOKEN_NOT_FOUND         = "리프레시 토큰이 존재하지 않습니다"
        const val INVALID_REFRESH_TOKEN           = "리프레시 토큰이 유효하지 않습니다"
        const val ACCESS_TOKEN_NOT_AUTHENTICATED  = "액세스 토큰 검증에 실패했습니다"
        const val REFRESH_TOKEN_NOT_AUTHENTICATED = "리프레시 토큰 검증에 실패했습니다"
    }
}