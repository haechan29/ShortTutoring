package org.softwaremaestro.data.login.tokenIssuerTest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenAuthenticator
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.AuthResult.*
import org.softwaremaestro.domain.mylogin.entity.AuthTokenApi
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.TotalTokenAuthenticator

class TotalTokenAuthenticatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val spyApi = spyk<AuthTokenApi>()
    val accessTokenAuthenticator = spyk(FakeAccessTokenAuthenticator, recordPrivateCalls = true) {
        every { api } returns spyApi
    }
    val refreshTokenAuthenticator = spyk(FakeRefreshTokenAuthenticator, recordPrivateCalls = true) {
        every { api } returns spyApi
    }

    val totalTokenAuthenticator = spyk<TotalTokenAuthenticator> {
        coEvery { authToken() } coAnswers {
            if (accessTokenAuthenticator.authToken() is Ok) {
                return@coAnswers ACCESS_TOKEN_IS_AUTHENTICATED
            }

            when (refreshTokenAuthenticator.authToken()) {
                is Ok -> ACCESS_TOKEN_IS_NOT_AUTHENTICATED
                is Failure -> REFRESH_TOKEN_IS_NOT_AUTHENTICATED
            }
        }
    }

    context("액세스 토큰과 리프레시 토큰을 검증한다") {
        test("액세스 토큰을 검증한다") {
            totalTokenAuthenticator.authToken()

            coVerify { accessTokenAuthenticator.authToken() }
        }

        test("액세스 토큰이 검증을 통과하면 액세스 토큰이 검증되었다는 결과를 반환한다") {
            coEvery { accessTokenAuthenticator.authToken() } returns mockk<Ok<Any>>()

            totalTokenAuthenticator.authToken() shouldBe ACCESS_TOKEN_IS_AUTHENTICATED
        }

        test("액세스 토큰이 검증을 통과하지 못하면 리프레시 토큰을 검증한다") {
            coEvery { accessTokenAuthenticator.authToken() } returns mockk<Failure>()

            totalTokenAuthenticator.authToken()

            coVerify { refreshTokenAuthenticator.authToken() }
        }

        context("리프레시 토큰이 검증을 통과하면") {
            coEvery { refreshTokenAuthenticator.authToken() } returns mockk<Ok<Any>>()

            val result = totalTokenAuthenticator.authToken()

            test("액세스 토큰이 검증에 실패했다는 결과를 반환한다") {
                result shouldBe ACCESS_TOKEN_IS_NOT_AUTHENTICATED
            }
        }

        context("리프레시 토큰이 검증을 통과하지 못하면") {
            coEvery { refreshTokenAuthenticator.authToken() } returns mockk<Failure>()

            val result = totalTokenAuthenticator.authToken()

            test("리프레시 토큰이 검증에 실패했다는 결과를 반환한다") {
                result shouldBe REFRESH_TOKEN_IS_NOT_AUTHENTICATED
            }
        }
    }
})