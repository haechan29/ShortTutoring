package org.softwaremaestro.data.login.test

import io.kotest.assertions.throwables.shouldThrow
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
import net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType
import org.softwaremaestro.data.mylogin.fake.FakeApi
import org.softwaremaestro.domain.mylogin.entity.Interceptor
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.Response
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.Server
import org.softwaremaestro.domain.mylogin.entity.result.DtoContainsNullFieldFailure
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.result.NetworkSuccess

class ApiTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val interceptor = mockk<Interceptor>(relaxed = true)

    val api = spyk(object: FakeApi(interceptor) {}, recordPrivateCalls = true) {
        every { this@spyk["toRequest"](ofType<RequestDto>()) } returns mockk<Request<RequestDto>>(relaxed = true)
        coEvery { this@spyk["sendToServer"](ofType<Request<RequestDto>>()) } returns mockk<NetworkSuccess<ResponseDto>>(relaxed = true)
    }

    context("API를 호출할 때") {
        api.sendRequest(mockk<RequestDto>(relaxed = true))

        test("요청을 서버로 전송한다") {
            coVerify { api["sendToServer"](ofType<Request<RequestDto>>()) }
        }
    }

    context("서버가 실패 응답을 반환하면") {
        val failure = mockk<NetworkFailure>(relaxed = true)

        coEvery { api["sendToServer"](ofType<Request<RequestDto>>()) } returns failure

        val result = api.sendRequest(mockk<RequestDto>(relaxed = true))

        test("실패 응답을 반환한다") {
            result shouldBe failure
        }
    }

    context("서버가 'DTO가 null 필드를 포함함' 실패를 반환하면") {
        val dtoWithNullField = mockk<ResponseDto>(relaxed = true) {
            every { containsNullField() } returns true
        }
        coEvery { api["sendToServer"](ofType<Request<RequestDto>>()) } returns NetworkSuccess(dtoWithNullField)

        val result = api.sendRequest(mockk<RequestDto>(relaxed = true))

        test("요청을 서버로 재전송한다") {
            coVerify (atLeast = 2) {
                api["sendToServer"](ofType<Request<RequestDto>>())
            }
        }

        context ("계속해서 'DTO가 null 필드를 포함함' 실패를 반환하면") {
            test("'DTO가 null 필드를 포함함' 실패를 반환한다") {
                result should beInstanceOf<DtoContainsNullFieldFailure>()
            }
        }
    }
})