package org.softwaremaestro.domain.fake_login.entity

interface Token: Validatable

sealed interface LoginToken: Token {
    companion object {
        const val ACCESS_TOKEN = "access_token"
    }
}
interface LoginAccessToken: LoginToken
interface LoginRefreshToken: LoginToken

object FakeLoginAccessToken: LoginAccessToken {
    override fun isValid() = true
}

object FakeLoginRefreshToken: LoginRefreshToken {
    override fun isValid() = true
}