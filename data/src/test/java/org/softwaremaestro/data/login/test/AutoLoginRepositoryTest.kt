package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.fake_login.impl.AutoLoginRepositoryImpl
import org.softwaremaestro.data.fake_login.legacy.AutoLoginApi
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.data.fake_login.dto.EmptyRequestDto

class AutoLoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val autoLoginApi = mockk<AutoLoginApi>(relaxed = true)

    val autoLoginRepositoryImpl = spyk(AutoLoginRepositoryImpl(autoLoginApi), recordPrivateCalls = true)

    context("자동 로그인할 때 서버가 실패 응답을 반환하면") {
        val serverFailure = mockk<NetworkFailure>(relaxed = true)

        coEvery { autoLoginApi.sendRequest(ofType<EmptyRequestDto>()) } returns serverFailure

        val result = autoLoginRepositoryImpl.autologin()

        test("서버가 반환한 실패를 반환한다") {
            result shouldBe serverFailure
        }
    }
})