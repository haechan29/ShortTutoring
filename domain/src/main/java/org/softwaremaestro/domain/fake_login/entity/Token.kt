package org.softwaremaestro.domain.fake_login.entity

interface Token: Validatable

sealed interface LoginToken: Token {
    val content: String

    override fun toString(): String

    companion object {
        const val ACCESS_TOKEN = "access token"
    }
}

interface LoginAccessToken: LoginToken
interface LoginRefreshToken: LoginToken