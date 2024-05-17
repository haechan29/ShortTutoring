package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.fake_login.dto.IssueLoginTokenRequestDto
import org.softwaremaestro.data.fake_login.dto.IssueTokenResponseDto
import org.softwaremaestro.data.fake_login.fake.AccessTokenIssuer
import org.softwaremaestro.data.fake_login.fake.FakeAccessTokenDao
import org.softwaremaestro.data.fake_login.fake.RefreshTokenIssuer
import org.softwaremaestro.data.fake_login.fake.FakeRefreshTokenDao
import org.softwaremaestro.data.fake_login.fake.FakeLoginTokenIssuer
import org.softwaremaestro.data.fake_login.legacy.IssueAccessTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueRefreshTokenApi
import org.softwaremaestro.data.fake_login.legacy.IssueLoginTokenApi
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.InvalidLoginInfo
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.domain.fake_login.result.LoginTokenNotFound

class LoginTokenIssuerTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val issueAccessTokenApi = mockk<IssueAccessTokenApi>(relaxed = true)
    val issueRefreshTokenApi = mockk<IssueRefreshTokenApi>(relaxed = true)
    val issueTokenApi = mockk<IssueLoginTokenApi>(relaxed = true)

    val tokenNotFound = mockk<LoginTokenNotFound>(relaxed = true)

    val accessTokenDao = mockk<FakeAccessTokenDao>(relaxed = true)
    val refreshTokenDao = mockk<FakeRefreshTokenDao>(relaxed = true)

    val accessTokenIssuer = spyk(
        AccessTokenIssuer(issueAccessTokenApi, accessTokenDao, refreshTokenDao),
        recordPrivateCalls = true) {

        coEvery { this@spyk["sendRequest"](ofType<IssueLoginTokenRequestDto>()) } returns NetworkSuccess(mockk<IssueTokenResponseDto>(relaxed = true))
    }

    val refreshTokenIssuer = spyk(
        RefreshTokenIssuer(issueRefreshTokenApi, accessTokenDao, refreshTokenDao),
        recordPrivateCalls = true
    ) {
        coEvery { this@spyk["sendRequest"](ofType<IssueLoginTokenRequestDto>()) } returns NetworkSuccess(mockk<IssueTokenResponseDto>(relaxed = true))
    }

    val loginTokenIssuer = spyk(object: FakeLoginTokenIssuer(
        tokenNotFound, issueTokenApi, accessTokenDao, refreshTokenDao
    ) {
        override suspend fun getDtoOrNull(): IssueLoginTokenRequestDto {
            return mockk<IssueLoginTokenRequestDto>(relaxed = true)
        }
    },recordPrivateCalls = true) {
        coEvery { this@spyk["sendRequest"](ofType<IssueLoginTokenRequestDto>()) } returns NetworkSuccess(mockk<IssueTokenResponseDto>(relaxed = true))
    }

    context("액세스 토큰을 발급할 때") {
        accessTokenIssuer.issueToken()

        test("리프레시 토큰을 로드한다") {
            coVerify { accessTokenIssuer["loadRefreshToken"]() }
        }
    }

    context("리프레시 토큰을 발급할 때") {
        refreshTokenIssuer.issueToken()

        test("로그인 정보를 입력받는다") {
            coVerify { refreshTokenIssuer["getLoginInfo"]() }
        }
    }

    context("리프레시 토큰을 로드하거나 로그인 정보를 입력받는데 실패하면") {
        coEvery { loginTokenIssuer["sendRequest"](ofType<IssueLoginTokenRequestDto>()) } returns mockk<NetworkFailure>(relaxed = true)

        val result = loginTokenIssuer.issueToken()

        test("토큰 발급을 실패 처리한다") {
            result should beInstanceOf<NetworkFailure>()
        }
    }

    context("리프레시 토큰을 발급할 때 로그인 정보가 올바르지 않다면") {
        coEvery { loginTokenIssuer["sendRequest"](ofType<IssueLoginTokenRequestDto>()) } returns InvalidLoginInfo

        val result = loginTokenIssuer.issueToken()

        test("토큰 발급을 로그인 정보가 유효하지 않음 실패 처리한다") {
            result shouldBe InvalidLoginInfo
        }
    }

    context("토큰을 저장하는데 실패하면") {
        coEvery { loginTokenIssuer["saveOrFail"](ofType<LoginToken>()) } returns mockk<NetworkFailure>(relaxed = true)

        val result = loginTokenIssuer.issueToken()

        test("토큰 발급을 실패 처리한다") {
            result should beInstanceOf<NetworkFailure>()
        }
    }

    context("API가 로그인 정보가 유효하지 않음 실패가 아닌 다른 실패 응답을 반환하면") {
        coEvery { loginTokenIssuer["sendRequest"](ofType<IssueLoginTokenRequestDto>()) } returns mockk<NetworkFailure>()

        loginTokenIssuer.issueToken()

        test("토큰 발급을 재요청한다") {
            coVerify(atLeast = 2) { loginTokenIssuer["sendRequest"](ofType<IssueLoginTokenRequestDto>()) }
        }
    }
})