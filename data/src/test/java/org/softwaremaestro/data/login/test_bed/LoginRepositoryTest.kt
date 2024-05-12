package org.softwaremaestro.data.login.test_bed

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
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.data.mylogin.fake.FakeMyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_LOGIN_INFO
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.Role

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val fakeLoginApi = mockk<LoginApi>(relaxed = true)

    val fakeMyLoginRepository = spyk(
        FakeMyLoginRepository(fakeLoginApi), recordPrivateCalls = true
    ) {
        every { this@spyk["isValid"](ofType<LoginRequestDto>()) } returns true
    }

    context("로그인 정보가 유효하지 않으면") {
        every { fakeMyLoginRepository["isValid"](ofType<LoginRequestDto>()) } returns false

        val result = fakeMyLoginRepository.login("", "")

        test("로그인 정보가 유효하지 않음 실패를 반환한다") {
            result should beInstanceOf<InvalidLoginInfo>()
        }
    }

    context("서버가 실패 응답을 반환하면") {
        every { fakeMyLoginRepository["isValid"](ofType<LoginRequestDto>()) } returns false

        val result = fakeMyLoginRepository.login("", "")

        test("로그인 정보가 유효하지 않음 실패 처리한다") {
            (result as NetworkFailure).message shouldBe INVALID_LOGIN_INFO
        }
    }

    context("서버가 성공 응답을 반환하면") {
        test("응답은 역할을 포함한다") {

        }

        test("학생 회원은 학생 홈 화면으로 이동한다") {
            val studentDto = mockk<LoginResponseDto>(relaxed = true) {
                every { role } returns Role.STUDENT
            }

            val successfulResult = mockk<NetworkSuccess<LoginResponseDto>>(relaxed = true) {
                every { dto } returns studentDto
            }

            coEvery { fakeLoginApi.sendRequest(any()) } returns successfulResult

            fakeMyLoginRepository.login("", "")

            // 학생 홈 화면으로 이동한다
        }
    }

    xcontext("자동 로그인한다") {
        xtest("Splash Activity에 진입하면 자동 로그인을 시작한다") {
            // TODO()
        }

        test("자동 로그인이 시작되면 액세스 토큰 인증을 시작한다") {
            fakeMyLoginRepository.autologin()

            coVerify { fakeMyLoginRepository["loadAccessToken"]() }
        }

        test("액세스 토큰 인증이 실패하면 리프레시 토큰 인증을 시작한다") {
            coEvery { fakeMyLoginRepository["loadAccessToken"]() } returns spyk<NetworkFailure>()

            fakeMyLoginRepository.autologin()

            coVerify { fakeMyLoginRepository["loadRefreshToken"]() }
        }
    }

    xcontext("토큰 발급에 실패하면 어떻게 처리한다") {

    }

    xcontext("토큰이 없으면 토큰을 발급한다") {

    }
})