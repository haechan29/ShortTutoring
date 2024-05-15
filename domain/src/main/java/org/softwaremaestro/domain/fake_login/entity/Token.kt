package org.softwaremaestro.domain.fake_login.entity

interface Token: Validatable

sealed interface LoginToken: Token
interface LoginAccessToken: LoginToken
interface LoginRefreshToken: LoginToken
interface LoginSubToken: LoginAccessToken, LoginRefreshToken

object FakeLoginAccessToken: LoginAccessToken {
    override fun isValid() = true
}

object FakeLoginRefreshToken: LoginRefreshToken {
    override fun isValid() = true
}