package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.beOfType
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
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidAccessToken
import org.softwaremaestro.domain.mylogin.entity.InvalidRefreshToken
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenNotFound

class TokenAuthenticatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("토큰 인증을 진행한다") {
        val tokenAuthenticator = spyk<FakeTokenAuthenticator>(recordPrivateCalls = true)
        val accessTokenAuthenticator = spyk<FakeAccessTokenAuthenticator>(recordPrivateCalls = true)
        val refreshTokenAuthenticator = spyk<FakeRefreshTokenAuthenticator>(recordPrivateCalls = true)

        val validToken = mockk<LoginAccessToken> {
            every { isValid() } returns true
        }

        val invalidToken = mockk<LoginToken> {
            every { isValid() } returns false
        }

        test("토큰 인증을 시작하면 저장된 토큰을 로드한다") {
            tokenAuthenticator.authToken()

            coVerify { tokenAuthenticator["readToken"]() }
        }

        context("토큰을 가지고 있지 않다면 토큰 인증을 실패 처리한다") {
            test("액세스 토큰을 가지고 있지 않다면 액세스 토큰 인증을 실패 처리한다") {
                coEvery { accessTokenAuthenticator["readToken"]() } returns null

                accessTokenAuthenticator.authToken() should beInstanceOf<Failure>()
            }

            test("리프레시 토큰을 가지고 있지 않다면 리프레시 토큰 인증을 실패 처리한다") {
                coEvery { refreshTokenAuthenticator["readToken"]() } returns null

                refreshTokenAuthenticator.authToken() should beInstanceOf<Failure>()
            }
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
            val token = spyk(LoginAccessToken(""))

            coEvery { tokenAuthenticator["readToken"]() } returns token

            tokenAuthenticator.authToken()

            verify { token.isValid() }
        }

        context("유효하지 않은 토큰을 가지고 있다면 토큰 인증을 실패 처리한다") {
            test("유효하지 않은 액세스 토큰을 가지고 있다면 액세스 토큰 인증을 실패 처리한다") {
                coEvery { accessTokenAuthenticator["readToken"]() } returns invalidToken

                accessTokenAuthenticator.authToken() should beInstanceOf<Failure>()
            }

            test("유효하지 않은 리프레시 토큰을 가지고 있다면 리프레시 토큰 인증을 실패 처리한다") {
                coEvery { refreshTokenAuthenticator["readToken"]() } returns invalidToken

                refreshTokenAuthenticator.authToken() should beInstanceOf<Failure>()
            }
        }

        context("토큰이 유효하지 않아 토큰 인증이 실패하면 이를 알린다") {
            test("액세스 토큰이 유효하지 않아 액세스 토큰 인증이 실패하면 이를 알린다") {
                coEvery { accessTokenAuthenticator["readToken"]() } returns invalidToken

                val result = accessTokenAuthenticator.authToken() as Failure
                result.message shouldBe InvalidAccessToken.message
            }

            test("리프레시 토큰이 유효하지 않아 리프레시 토큰 인증이 실패하면 이를 알린다") {
                coEvery { refreshTokenAuthenticator["readToken"]() } returns invalidToken

                val result = refreshTokenAuthenticator.authToken() as Failure
                result.message shouldBe InvalidRefreshToken.message
            }
        }

        test("유효한 토큰을 가지고 있다면 토큰 인증을 성공 처리한다") {
            coEvery { tokenAuthenticator["readToken"]() } returns validToken

            tokenAuthenticator.authToken() should beInstanceOf<Ok<Any>>()
        }
    }
})