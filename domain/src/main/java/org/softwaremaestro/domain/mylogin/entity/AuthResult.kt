package org.softwaremaestro.domain.mylogin.entity

enum class AuthResult {
    ACCESS_TOKEN_IS_AUTHENTICATED,
    ACCESS_TOKEN_IS_NOT_AUTHENTICATED,
    REFRESH_TOKEN_IS_NOT_AUTHENTICATED
}