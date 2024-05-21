package org.softwaremaestro.data.fake_login.fake

import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken

class FakeLoginAccessToken(
    override val content: String = "fake access token"
): LoginAccessToken {
    override fun isValid() = true

    override fun toString() = content
}

class FakeLoginRefreshToken(
    override val content: String = "fake refresh token"
): LoginRefreshToken {
    override fun isValid() = true

    override fun toString() = content
}