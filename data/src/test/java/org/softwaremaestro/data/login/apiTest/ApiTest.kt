package org.softwaremaestro.data.login.apiTest

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
import org.softwaremaestro.data.mylogin.fake.FakeApi
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.Server

class ApiTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val tokenRepository = mockk<TokenRepository>(relaxed = true)
    val request = mockk<Request>(relaxed = true)
    val server = mockk<Server>(relaxed = true) {
        coEvery { sendRequest(any()) } returns mockk<NetworkResult<Any>>()
    }

    val api = spyk(FakeApi(tokenRepository, request, server))

    xcontext("요청을 전송할 때") {
        context("토큰을 로드하는데 실패하면") {
            coEvery { tokenRepository.load() } returns mockk<Failure>()

            val result = api.sendRequest()

            test("실패 응답을 반환한다") {
                result should beInstanceOf<Failure>()
            }

            test("TokenRepository가 반환한 실패를 반환한다") {
                result shouldBe tokenRepository.load()
            }
        }

        context ("토큰을 로드하는데 성공하면") {
            val token = mockk<LoginToken>(relaxed = true)
            coEvery { tokenRepository.load() } returns Ok(token)

            val result = api.sendRequest()

            test("토큰을 요청 헤더에 삽입한다") {
                verify { request.addToHeader(any()) }
            }

            test("요청을 서버로 전송한다") {
                coVerify { server.sendRequest(request) }
            }

            test("서버가 반환한 응답을 반환한다") {
                result shouldBe server.sendRequest(request)
            }
        }
    }

    afterEach { unmockkAll() }
})