package org.softwaremaestro.data.mylogin.fake

class FailureHandler {
    /*
    context("토큰을 가지고 있지 않아 토큰 인증이 실패하면 이를 알린다") {
            test("액세스 토큰을 가지고 있지 않아 액세스 토큰 인증이 실패하면 이를 알린다") {
                coEvery { accessTokenAuthenticator["readToken"]() } returns null

                val result = accessTokenAuthenticator.authToken() as Failure
                result.message shouldBe AccessTokenNotFound.message
            }

            test("리프레시 토큰을 가지고 있지 않아 리프레시 토큰 인증이 실패하면 이를 알린다") {
                coEvery { refreshTokenAuthenticator["readToken"]() } returns null

                val result = refreshTokenAuthenticator.authToken() as Failure
                result.message shouldBe RefreshTokenNotFound.message
            }
        }

        context("토큰이 유효하지 않아 토큰 인증이 실패하면 이를 알린다") {
            every { token.isValid() } returns false

            test("액세스 토큰이 유효하지 않아 액세스 토큰 인증이 실패하면 이를 알린다") {
                coEvery { accessTokenAuthenticator["readToken"]() } returns token

                val result = accessTokenAuthenticator.authToken() as Failure
                result.message shouldBe InvalidAccessToken.message
            }

            test("리프레시 토큰이 유효하지 않아 리프레시 토큰 인증이 실패하면 이를 알린다") {
                coEvery { refreshTokenAuthenticator["readToken"]() } returns token

                val result = refreshTokenAuthenticator.authToken() as Failure
                result.message shouldBe InvalidRefreshToken.message
            }
        }
     */
}