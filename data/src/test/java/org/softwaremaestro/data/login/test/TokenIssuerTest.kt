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
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenIssuer
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenIssuer
import org.softwaremaestro.data.mylogin.fake.FakeTokenIssuer
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.result.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.dto.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.dto.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.result.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.result.TokenNotFound

class TokenIssuerTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val accessTokenRepository = mockk<TokenRepository<LoginAccessToken>>(relaxed = true)
    val tokenNotFound = mockk<TokenNotFound<LoginToken>>(relaxed = true)

    val accessTokenIssuer = spyk(object: FakeAccessTokenIssuer(accessTokenRepository) {
        override fun getTokens(body: ResponseDto): List<LoginAccessToken> {
            return listOf(mockk<LoginAccessToken>())
        }
    }, recordPrivateCalls = true) {
        every { this@spyk["toDto"](ofType<LocalTokenResponseDto>()) } returns mockk<LoginRequestDto>(relaxed = true)
        coEvery { this@spyk["sendRequest"](ofType<LoginRequestDto>()) } returns NetworkSuccess(mockk<LoginResponseDto>(relaxed = true))
        coEvery { this@spyk["saveTokenToStorage"](ofType<LoginToken>()) } returns mockk<LoginToken>(relaxed = true)
    }

    val refreshTokenIssuer = spyk(object: FakeRefreshTokenIssuer() {
        override fun getTokens(body: ResponseDto): List<LoginRefreshToken> {
            return listOf(mockk<LoginRefreshToken>())
        }
    }, recordPrivateCalls = true) {
        coEvery { this@spyk["getLoginInfo"]() } returns NetworkSuccess(mockk<LocalTokenResponseDto>(relaxed = true))
        every { this@spyk["toDto"](ofType<LocalTokenResponseDto>()) } returns mockk<LoginRequestDto>(relaxed = true)
        coEvery { this@spyk["sendRequest"](ofType<LoginRequestDto>()) } returns NetworkSuccess(mockk<LoginResponseDto>(relaxed = true))
        coEvery { this@spyk["saveTokenToStorage"](ofType<LoginToken>()) } returns mockk<LoginToken>(relaxed = true)
    }

    val tokenIssuer = spyk(object: FakeTokenIssuer<LoginToken>(tokenNotFound) {
        override suspend fun getLocalTokenDtoResult(): NetworkResult<LocalTokenResponseDto> {
            return NetworkSuccess(mockk<LocalTokenResponseDto>(relaxed = true))
        }

        override fun getTokens(body: ResponseDto): List<LoginToken> {
            return listOf(mockk<LoginToken>())
        }
    },recordPrivateCalls = true) {
        every { this@spyk["toDto"](ofType<LocalTokenResponseDto>()) } returns mockk<LoginRequestDto>(relaxed = true)
        coEvery { this@spyk["sendRequest"](ofType<LoginRequestDto>()) } returns NetworkSuccess(mockk<LoginResponseDto>(relaxed = true))
        coEvery { this@spyk["saveTokenToStorage"](ofType<LoginToken>()) } returns mockk<LoginToken>(relaxed = true)
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
        coEvery { tokenIssuer["getLocalTokenDtoResult"]() } returns mockk<NetworkFailure>(relaxed = true)

        val result = tokenIssuer.issueToken()

        test("토큰 발급을 실패 처리한다") {
            result should beInstanceOf<NetworkFailure>()
        }
    }

    context("API가 토큰 발급 성공 응답을 반환하면") {
        tokenIssuer.issueToken()

        test("토큰을 포함하는지 확인한다") {
            verify { tokenIssuer["getTokens"](ofType<ResponseDto>()) }
        }
    }

    context("API 응답이 토큰을 포함하지 않으면") {
        every { tokenIssuer["getTokens"](ofType<ResponseDto>()) } returns emptyList<LoginToken>()

        tokenIssuer.issueToken()

        test("토큰을 저장한다") {
            coVerify(exactly = 0) { tokenIssuer["saveTokenToStorage"](ofType<LoginToken>()) }
        }
    }

    context("리프레시 토큰을 발급할 때 로그인 정보가 올바르지 않다면") {
        coEvery { tokenIssuer["sendRequest"](ofType<LoginRequestDto>()) } returns InvalidLoginInfo

        val result = tokenIssuer.issueToken()

        test("토큰 발급을 로그인 정보가 유효하지 않음 실패 처리한다") {
            result shouldBe InvalidLoginInfo
        }
    }

    context("API가 로그인 정보가 유효하지 않음 실패가 아닌 다른 실패 응답을 반환하면") {
        coEvery { tokenIssuer["sendRequest"](ofType<LoginRequestDto>()) } returns mockk<NetworkFailure>()

        tokenIssuer.issueToken()

        test("토큰 발급을 재요청한다") {
            coVerify(atLeast = 2) { tokenIssuer["sendRequest"](ofType<LoginRequestDto>()) }
        }
    }
})