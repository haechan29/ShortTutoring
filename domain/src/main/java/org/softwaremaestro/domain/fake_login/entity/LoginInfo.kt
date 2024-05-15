package org.softwaremaestro.domain.fake_login.entity

data class LoginInfo(
    val accessToken: LoginAccessToken?,
    val refreshToken: LoginRefreshToken?
)