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
import io.mockk.unmockkAll
import io.mockk.verify
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.data.mylogin.fake.FakeTokenIssuer
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.IssueTokenApi
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

class TokenIssuerTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val mockApi = mockk<IssueTokenApi>(relaxed = true)
    val tokenNotFound = mockk<TokenNotFound>(relaxed = true)
    val fakeTokens = listOf(mockk<LoginToken>(relaxed = true), mockk<LoginToken>(relaxed = true))

    val tokenIssuer = spyk(object: FakeTokenIssuer<LoginToken>(mockApi, tokenNotFound) {
        override suspend fun getLoginRequestDto(): LoginRequestDto {
            return mockk<LoginRequestDto>(relaxed = true)
        }

        override suspend fun sendRequest(dto: LoginRequestDto): NetworkResult<ResponseDto> {
            return mockk<NetworkResult<ResponseDto>>(relaxed = true)
        }

        override fun getTokens(body: ResponseDto): List<LoginToken> {
            return fakeTokens
        }

        override suspend fun saveToken(token: LoginToken) {}
    },recordPrivateCalls = true)

    context("API가 토큰 발급 성공 응답을 반환하면") {
        coEvery { tokenIssuer["getLoginRequestDto"]() } returns mockk<LoginRequestDto>()

        val mockOk = mockk<Ok<Any>>(relaxed = true) {
            every { body } returns mockk<ResponseDto>(relaxed = true)
        }
        coEvery { tokenIssuer["sendRequest"](ofType<LoginRequestDto>()) } returns mockOk

        test("토큰을 포함하는지 확인한다") {
            every { tokenIssuer["getTokens"](ofType<ResponseDto>()) } returns mockk<List<LoginToken>>(relaxed = true)

            tokenIssuer.issueToken()

            verify { tokenIssuer["getTokens"](ofType<ResponseDto>()) }
        }

        test("토큰을 저장한다") {
            val nonEmptyTokens = listOf(mockk<LoginToken>())
            every { tokenIssuer["getTokens"](ofType<ResponseDto>()) } returns nonEmptyTokens

            tokenIssuer.issueToken()

            coVerify { tokenIssuer["saveToken"](ofType<LoginToken>()) }
        }
    }

    context("리프레시 토큰을 발급할 때 로그인 정보가 올바르지 않다면") {
        coEvery { tokenIssuer["getLoginRequestDto"]() } returns mockk<LoginRequestDto>()
        coEvery { tokenIssuer["sendRequest"](ofType<LoginRequestDto>()) } returns InvalidLoginInfo

        test("토큰 발급을 로그인 정보가 유효하지 않음 실패 처리한다") {
            val result = tokenIssuer.issueToken()
            result shouldBe InvalidLoginInfo
        }
    }

    context("API가 로그인 정보가 유효하지 않음 실패가 아닌 다른 실패 응답을 반환하면") {
        test("토큰 발급을 3회 재요청한다") {
            coEvery { tokenIssuer["getLoginRequestDto"]() } returns mockk<LoginRequestDto>()
            coEvery { tokenIssuer["sendRequest"](ofType<LoginRequestDto>()) } returns mockk<Failure>()

            tokenIssuer.issueToken()

            coVerify(atLeast = 3, atMost = 5) { tokenIssuer["sendRequest"](ofType<LoginRequestDto>()) }
        }
    }

    context("토큰 발급을 재요청했을 때 성공 응답을 받으면") {
        coEvery { tokenIssuer["getLoginRequestDto"]() } returns mockk<LoginRequestDto>()

        coEvery { tokenIssuer["sendRequest"](ofType<LoginRequestDto>()) } returns mockk<Ok<Any>> {
            every { body } returns mockk<ResponseDto>(relaxed = true)
        }

        val nonEmptyTokens = listOf(mockk<LoginToken>())
        every { tokenIssuer["getTokens"](ofType<ResponseDto>()) } returns nonEmptyTokens

        test("토큰 발급을 성공 처리한다") {
            val result = tokenIssuer.issueToken()
            result should beInstanceOf<Ok<Any>>()
        }
    }

    afterEach { unmockkAll() }
})