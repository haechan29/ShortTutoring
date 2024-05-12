package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import org.softwaremaestro.data.mylogin.fake.FakeTokenAuthenticator
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated

class TokenAuthenticatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val accessTokenRepository = mockk<TokenRepository<LoginAccessToken>>(relaxed = true)
    val refreshTokenRepository = mockk<TokenRepository<LoginRefreshToken>>(relaxed = true)

    val tokenAuthenticator = spyk(
        objToCopy = object: FakeTokenAuthenticator(accessTokenRepository, refreshTokenRepository) {},
        recordPrivateCalls = true
    ) {
        coEvery { this@spyk["loadAccessToken"]() } returns mockk<NetworkFailure>(relaxed = true)
        coEvery { this@spyk["loadRefreshToken"]() } returns mockk<NetworkFailure>(relaxed = true)
    }

    test("액세스 토큰을 검증한다") {
        tokenAuthenticator.authToken()

        coVerify { tokenAuthenticator["loadAccessToken"]() }
    }

    context("액세스 토큰이 검증을 통과하면") {
        coEvery { tokenAuthenticator["loadAccessToken"]() } returns mockk<NetworkSuccess<LocalTokenResponseDto>>(relaxed = true)

        val result = tokenAuthenticator.authToken()

        test("액세스 토큰이 검증되었다는 결과를 반환한다") {
            result shouldBe AccessTokenIsAuthenticated
        }
    }

    context("액세스 토큰이 검증을 통과하지 못하면") {
        tokenAuthenticator.authToken()

        test("리프레시 토큰을 검증한다") {
            coVerify { tokenAuthenticator["loadRefreshToken"]() }
        }
    }

    context("리프레시 토큰이 검증을 통과하면") {
        coEvery { tokenAuthenticator["loadRefreshToken"]() } returns mockk<NetworkSuccess<LocalTokenResponseDto>>()

        val result = tokenAuthenticator.authToken()

        test("액세스 토큰이 검증에 실패했다는 결과를 반환한다") {
            result shouldBe AccessTokenIsNotAuthenticated
        }
    }

    context("리프레시 토큰이 검증을 통과하지 못하면") {
        val result = tokenAuthenticator.authToken()

        test("리프레시 토큰이 검증에 실패했다는 결과를 반환한다") {
            result shouldBe RefreshTokenIsNotAuthenticated
        }
    }
})