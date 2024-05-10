package org.softwaremaestro.domain.mylogin.entity

interface Token: Validatable

sealed interface LoginToken: Token
interface LoginAccessToken: LoginToken
interface LoginRefreshToken: LoginToken