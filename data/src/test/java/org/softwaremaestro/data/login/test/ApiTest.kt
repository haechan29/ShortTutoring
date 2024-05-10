package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import org.softwaremaestro.data.mylogin.fake.FakeApi
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.RequestDto
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Server

class ApiTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val api = spyk(object: FakeApi(mockk<Server>()) {
        override fun toRequest(dto: RequestDto): Request {
            return mockk<Request>()
        }

        override suspend fun addTokenToRequestHeader() {}

        override suspend fun sendToServer(request: Request): NetworkResult<ResponseDto> {
            return mockk<NetworkResult<ResponseDto>>()
        }
    }, recordPrivateCalls = true)

    context("요청을 전송할 때") {
        test("토큰을 요청 헤더에 삽입한다") {
            api.sendRequest(mockk<RequestDto>())

            coVerify { api.addTokenToRequestHeader() }
        }

        test("요청을 서버로 전송한다") {
            api.sendRequest(mockk<RequestDto>())

            coVerify { api["sendToServer"](ofType<Request>()) }
        }

        test("서버가 반환한 응답을 반환한다") {
            val serverResult = mockk<NetworkResult<ResponseDto>>()

            coEvery { api["sendToServer"](ofType<Request>()) } returns serverResult

            val result = api.sendRequest(mockk<RequestDto>())

            result shouldBe serverResult
        }
    }

    afterEach { unmockkAll() }
})