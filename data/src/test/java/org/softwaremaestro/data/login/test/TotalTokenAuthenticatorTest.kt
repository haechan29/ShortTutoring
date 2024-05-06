package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenAuthenticator
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenAuthenticator
import org.softwaremaestro.data.mylogin.fake.FakeTotalTokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AuthResult
import org.softwaremaestro.domain.mylogin.entity.AuthResult.*
import org.softwaremaestro.domain.mylogin.entity.AuthTokenApi
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.TotalTokenAuthenticator

class TotalTokenAuthenticatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    mockkObject(FakeAccessTokenAuthenticator, recordPrivateCalls = true)
    mockkObject(FakeRefreshTokenAuthenticator, recordPrivateCalls = true)

    mockkObject(FakeTotalTokenAuthenticator)

    context("액세스 토큰과 리프레시 토큰을 검증한다") {
        test("액세스 토큰을 검증한다") {
            FakeTotalTokenAuthenticator.authToken()

            coVerify { FakeAccessTokenAuthenticator.authToken() }
        }

        test("액세스 토큰이 검증을 통과하면 액세스 토큰이 검증되었다는 결과를 반환한다") {
            coEvery { FakeAccessTokenAuthenticator.authToken() } returns mockk<Ok<Any>>()

            FakeTotalTokenAuthenticator.authToken() shouldBe AccessTokenIsAuthenticated
        }


        test("액세스 토큰이 검증을 통과하지 못하면 리프레시 토큰을 검증한다") {
            coEvery { FakeAccessTokenAuthenticator.authToken() } returns mockk<Failure>()

            FakeTotalTokenAuthenticator.authToken()

            coVerify { FakeRefreshTokenAuthenticator.authToken() }
        }

        context("리프레시 토큰이 검증을 통과하면") {
            coEvery { FakeRefreshTokenAuthenticator.authToken() } returns mockk<Ok<Any>>()

            val result = FakeTotalTokenAuthenticator.authToken()

            test("액세스 토큰이 검증에 실패했다는 결과를 반환한다") {
                result shouldBe AccessTokenIsNotAuthenticated
            }
        }

        context("리프레시 토큰이 검증을 통과하지 못하면") {
            coEvery { FakeRefreshTokenAuthenticator.authToken() } returns mockk<Failure>()

            val result = FakeTotalTokenAuthenticator.authToken()

            test("리프레시 토큰이 검증에 실패했다는 결과를 반환한다") {
                result shouldBe RefreshTokenIsNotAuthenticated
            }
        }
    }

    afterEach { unmockkAll() }
})