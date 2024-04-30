package org.softwaremaestro.data.login.fake

import org.softwaremaestro.data.mylogin.AccessToken
import org.softwaremaestro.data.mylogin.RefreshToken

data class FakeAccessToken(val content: String): AccessToken
data class FakeRefreshToken(val content: String): RefreshToken