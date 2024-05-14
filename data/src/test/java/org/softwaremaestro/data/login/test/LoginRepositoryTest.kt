package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.data.mylogin.fake.FakeMyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.AutoLoginApi
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.dto.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkSuccess

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val fakeLoginApi = mockk<LoginApi>(relaxed = true)
    val fakeAutoLoginApi = mockk<AutoLoginApi>(relaxed = true)

    val fakeMyLoginRepository = spyk(
        FakeMyLoginRepository(fakeLoginApi, fakeAutoLoginApi), recordPrivateCalls = true
    )

    // 로그인
    context("로그인할 때 서버가 실패 응답을 반환하면") {
        val serverFailure = mockk<NetworkFailure>(relaxed = true)

        coEvery { fakeMyLoginRepository["login"](ofType<String>(), ofType<String>()) } returns serverFailure

        val result = fakeMyLoginRepository.login("", "")

        test("서버가 반환한 실패를 반환한다") {
            result shouldBe serverFailure
        }
    }

    // 자동 로그인
    context("자동 로그인할 때 서버가 실패 응답을 반환하면") {
        val serverFailure = mockk<NetworkFailure>(relaxed = true)

        coEvery { fakeMyLoginRepository["autologin"]() } returns serverFailure

        val result = fakeMyLoginRepository.autologin()

        test("서버가 반환한 실패를 반환한다") {
            result shouldBe serverFailure
        }
    }
})