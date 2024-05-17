package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.fake_login.fake.FakeInterceptor
import org.softwaremaestro.data.fake_login.legacy.Request
import org.softwaremaestro.data.fake_login.legacy.Response
import org.softwaremaestro.data.fake_login.legacy.Server
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.data.fake_login.dto.ResponseDto
import org.softwaremaestro.data.fake_login.legacy.LoginTokenInjector
import org.softwaremaestro.domain.fake_login.result.AccessTokenNotFound
import org.softwaremaestro.domain.fake_login.result.DtoContainsNullFieldFailure
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess

class InterceptorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val tokenInjector = mockk<LoginTokenInjector>(relaxed = true)
    val server = mockk<Server<RequestDto, ResponseDto>>(relaxed = true)

    val interceptor = spyk(
        objToCopy = object: FakeInterceptor<RequestDto, ResponseDto>(tokenInjector, server) {},
        recordPrivateCalls = true
    ) {
        coEvery { this@spyk["injectToken"](ofType<Request<RequestDto>>()) } returns NetworkSuccess(mockk<ResponseDto>(relaxed = true))
        coEvery { this@spyk["sendToServer"](ofType<Request<RequestDto>>()) } returns mockk<Response<ResponseDto>>(relaxed = true) {
            every { body } returns NetworkSuccess(mockk<ResponseDto>(relaxed = true))
        }
    }

    context("서버로 요청을 전송할 때") {
        interceptor.intercept(mockk<Request<RequestDto>>(relaxed = true))

        test("토큰을 주입한다") {
            coVerify { interceptor["injectToken"](ofType<Request<RequestDto>>()) }
        }
    }

    context("토큰 주입에 실패하면") {
        coEvery { interceptor["injectToken"](ofType<Request<RequestDto>>()) } returns mockk<NetworkFailure>(relaxed = true)

        val result = interceptor.intercept(mockk<Request<RequestDto>>(relaxed = true))

        test("'액세스 토큰이 발견되지 않음' 실패를 반환한다") {
            result should beInstanceOf<AccessTokenNotFound>()
        }
    }

    context("서버가 성공 응답을 반환했는데 DTO가 null 필드를 포함하면") {
        val dtoWithNullField = mockk<ResponseDto>(relaxed = true) {
            every { containsNullField() } returns true
        }

        coEvery { interceptor["sendToServer"](ofType<Request<RequestDto>>()) } returns mockk<Response<ResponseDto>>(relaxed = true) {
            every { body } returns NetworkSuccess(dtoWithNullField)
        }

        val result = interceptor.intercept(mockk<Request<RequestDto>>(relaxed = true))

        test("'DTO가 null 필드를 포함함' 실패를 반환한다") {
            result should beInstanceOf<DtoContainsNullFieldFailure>()
        }
    }
})