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
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.RequestDto
import org.softwaremaestro.domain.mylogin.entity.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Server

class ApiTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val server = mockk<Server>(relaxed = true)

    val api = spyk(object: FakeApi(server) {}, recordPrivateCalls = true) {
        every { this@spyk["toRequest"](ofType<RequestDto>()) } returns mockk<Request>(relaxed = true)
        coEvery { this@spyk["sendToServer"](ofType<Request>()) } returns mockk<NetworkResult<ResponseDto>>(relaxed = true)
    }

    context("API를 호출할 때") {
        api.sendRequest(mockk<RequestDto>(relaxed = true))

        test("요청을 서버로 전송한다") {
            coVerify { api["sendToServer"](ofType<Request>()) }
        }
    }

    context("서버가 응답을 반환하면") {
        val serverResult = mockk<NetworkResult<ResponseDto>>(relaxed = true)

        coEvery { api["sendToServer"](ofType<Request>()) } returns serverResult

        val result = api.sendRequest(mockk<RequestDto>(relaxed = true))

        test("서버가 반환한 응답을 반환한다") {
            result shouldBe serverResult
        }
    }
})