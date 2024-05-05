package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenAuthenticator
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenAuthenticator
import org.softwaremaestro.data.mylogin.fake.FakeTokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.AuthTokenApi
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidAccessToken
import org.softwaremaestro.domain.mylogin.entity.InvalidRefreshToken
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

class TokenAuthenticatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("토큰 인증을 진행한다") {
        val spyApi = spyk<AuthTokenApi>()
        val tokenAuthenticator = spyk<FakeTokenAuthenticator>(recordPrivateCalls = true) {
            every { api } returns spyApi
            every { tokenNotFoundFailure } returns spyk<TokenNotFound>()
            every { invalidTokenFailure } returns spyk<InvalidToken>()
        }
        val accessTokenAuthenticator = spyk(FakeAccessTokenAuthenticator, recordPrivateCalls = true) {
            every { api } returns spyApi
        }
        val refreshTokenAuthenticator = spyk(FakeRefreshTokenAuthenticator, recordPrivateCalls = true) {
            every { api } returns spyApi
        }

        val token = mockk<LoginAccessToken>(relaxed = true)

        test("토큰 인증을 시작하면 저장된 토큰을 로드한다") {
            tokenAuthenticator.authToken()

            coVerify { tokenAuthenticator["readToken"]() }
        }

        context("토큰을 가지고 있지 않다면 토큰 인증을 실패 처리한다") {
            coEvery { tokenAuthenticator["readToken"]() } returns null

            tokenAuthenticator.authToken() should beInstanceOf<Failure>()
        }

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

        test("토큰을 가지고 있다면 유효성을 확인한다") {
            coEvery { tokenAuthenticator["readToken"]() } returns token

            tokenAuthenticator.authToken()

            verify { token.isValid() }
        }

        context("유효하지 않은 토큰을 가지고 있다면 토큰 인증을 실패 처리한다") {
            every { token.isValid() } returns false
            coEvery { tokenAuthenticator["readToken"]() } returns token

            tokenAuthenticator.authToken() should beInstanceOf<Failure>()
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

        context("유효한 토큰을 가지고 있다면") {
            every { token.isValid() } returns true
            coEvery { tokenAuthenticator["readToken"]() } returns token

            test("API를 호출한다") {
                tokenAuthenticator.authToken()

                coVerify { spyApi.authToken() }
            }

            test("API가 반환한 응답을 반환한다") {
                coEvery { spyApi.authToken() } returns mockk<NetworkResult<Any>>(relaxed = true)

                tokenAuthenticator.authToken() shouldBe spyApi.authToken()
            }
        }
    }
})