package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import org.softwaremaestro.data.mylogin.fake.FakeApi
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.RequestDto

class ApiTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val mockDto = mockk<RequestDto>()
    val mockRequest = mockk<Request>()
    val serverResult = mockk<NetworkResult<Any>>()

    val api = spyk<FakeApi> {
        every { toRequest(mockDto) } returns mockRequest

        coEvery { server.send(mockRequest) } returns serverResult
    }

    context("요청을 전송할 때") {
        val result = api.sendRequest(mockDto)

        test("토큰을 요청 헤더에 삽입한다") {
            coVerify { api.addTokenToRequestHeader() }
        }

        test("요청을 서버로 전송한다") {
            coVerify { api.server.send(any()) }
        }

        test("서버가 반환한 응답을 반환한다") {
            result shouldBe serverResult
        }
    }

    afterEach { unmockkAll() }
})