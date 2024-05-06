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
import org.softwaremaestro.data.mylogin.fake.FakeAuthAccessTokenApi
import org.softwaremaestro.data.mylogin.fake.FakeAuthRefreshTokenApi
import org.softwaremaestro.data.mylogin.fake.FakeTokenIssuer
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AuthFailure
import org.softwaremaestro.domain.mylogin.entity.AuthResult.*
import org.softwaremaestro.domain.mylogin.entity.AuthTokenApi
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated

class TokenIssuerTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    mockkObject(FakeAuthAccessTokenApi)
    mockkObject(FakeAuthRefreshTokenApi)

    val tokenIssuer = spyk<FakeTokenIssuer>(recordPrivateCalls = true)

    val failureToIssueAccessToken = mockk<AccessTokenIsNotAuthenticated>()
    val failureToIssueRefreshToken = mockk<RefreshTokenIsNotAuthenticated>()

    context("액세스 토큰이 검증에 실패하면") {
        val result = tokenIssuer.issueToken(failureToIssueAccessToken)

        test("액세스 토큰 발급을 요청한다") {
            coVerify { tokenIssuer["issueAccessToken"]() }
        }

        test("액세스 토큰 발급 API를 호출한다") {
            coVerify { FakeAuthAccessTokenApi.authToken() }
        }

        test("API가 반환한 응답을 반환한다") {
            result shouldBe FakeAuthAccessTokenApi.authToken()
        }
    }

    context("리프레시 토큰이 검증에 실패하면") {
        val result = tokenIssuer.issueToken(failureToIssueRefreshToken)

        test("액세스 토큰과 리프레시 토큰 발급을 요청한다") {
            coVerify { tokenIssuer["issueAccessAndRefreshToken"]() }
        }

        test("리프레시 토큰 발급 API를 호출한다") {
            coVerify { FakeAuthRefreshTokenApi.authToken() }
        }

        test("API가 반환한 응답을 반환한다") {
            result shouldBe FakeAuthRefreshTokenApi.authToken()
        }
    }

    context("토큰 발급이 실패하면") {
        test("액세스 토큰 발급이 실패하면 토큰 발급을 재요청한다") {
            coEvery { tokenIssuer["issueAccessToken"]() } returns mockk<Failure>()

            tokenIssuer.issueToken(failureToIssueAccessToken)

            coVerify(atLeast = 3) { tokenIssuer["issueAccessToken"]() }
        }

        test("리프레시 토큰 발급이 실패하면 토큰 발급을 재요청한다") {
            coEvery { tokenIssuer["issueAccessAndRefreshToken"]() } returns mockk<Failure>()

            tokenIssuer.issueToken(failureToIssueRefreshToken)

            coVerify(atLeast = 3) { tokenIssuer["issueAccessAndRefreshToken"]() }
        }
    }

    context("토큰 발급을 3회 이상 요청했다면") {
        test("액세스 토큰 발급을 3회 이상 시도했다면 토큰 발급을 실패 처리한다") {
            coEvery { tokenIssuer["issueAccessToken"]() } returns mockk<Failure>()

            tokenIssuer.issueToken(failureToIssueAccessToken)

            coVerify(atMost = 5) { tokenIssuer["issueAccessToken"]() }
        }

        test("액세스 토큰 발급을 3회 이상 요청했다면 토큰 발급을 실패 처리한다") {
            coEvery { tokenIssuer["issueAccessAndRefreshToken"]() } returns mockk<Failure>()

            tokenIssuer.issueToken(failureToIssueRefreshToken)

            coVerify(atMost = 5) { tokenIssuer["issueAccessAndRefreshToken"]() }
        }
    }

    afterEach { unmockkAll() }
})