package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.fake_login.fake.FakeLoginTokenAuthenticator
import org.softwaremaestro.domain.fake_login.LoginTokenStorageRepository
import org.softwaremaestro.domain.fake_login.result.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.fake_login.result.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.data.fake_login.dto.LocalTokenResponseDto
import org.softwaremaestro.domain.fake_login.AccessTokenStorageRepository
import org.softwaremaestro.domain.fake_login.RefreshTokenStorageRepository
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.domain.fake_login.result.RefreshTokenIsNotAuthenticated

class TokenAuthenticatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val accessTokenRepository = mockk<AccessTokenStorageRepository>(relaxed = true)
    val refreshTokenRepository = mockk<RefreshTokenStorageRepository>(relaxed = true)

    val tokenAuthenticator = spyk(
        objToCopy = FakeLoginTokenAuthenticator(accessTokenRepository, refreshTokenRepository),
        recordPrivateCalls = true
    ) {
        coEvery { this@spyk["loadAccessToken"]() } returns mockk<NetworkFailure>(relaxed = true)
        coEvery { this@spyk["loadRefreshToken"]() } returns mockk<NetworkFailure>(relaxed = true)
    }

    test("액세스 토큰을 검증한다") {
        tokenAuthenticator.authLoginToken()

        coVerify { tokenAuthenticator["loadAccessToken"]() }
    }

    context("액세스 토큰이 검증을 통과하면") {
        coEvery { tokenAuthenticator["loadAccessToken"]() } returns mockk<NetworkSuccess<LocalTokenResponseDto>>(relaxed = true)

        val result = tokenAuthenticator.authLoginToken()

        test("액세스 토큰이 검증되었다는 결과를 반환한다") {
            result shouldBe AccessTokenIsAuthenticated
        }
    }

    context("액세스 토큰이 검증을 통과하지 못하면") {
        tokenAuthenticator.authLoginToken()

        test("리프레시 토큰을 검증한다") {
            coVerify { tokenAuthenticator["loadRefreshToken"]() }
        }
    }

    context("리프레시 토큰이 검증을 통과하면") {
        coEvery { tokenAuthenticator["loadRefreshToken"]() } returns mockk<NetworkSuccess<LocalTokenResponseDto>>()

        val result = tokenAuthenticator.authLoginToken()

        test("액세스 토큰이 검증에 실패했다는 결과를 반환한다") {
            result shouldBe AccessTokenIsNotAuthenticated
        }
    }

    context("리프레시 토큰이 검증을 통과하지 못하면") {
        val result = tokenAuthenticator.authLoginToken()

        test("리프레시 토큰이 검증에 실패했다는 결과를 반환한다") {
            result shouldBe RefreshTokenIsNotAuthenticated
        }
    }
})