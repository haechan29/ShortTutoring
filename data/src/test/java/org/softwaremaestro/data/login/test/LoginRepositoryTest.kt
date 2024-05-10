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
import org.softwaremaestro.data.mylogin.dto.LoginRequestDto
import org.softwaremaestro.data.mylogin.fake.FakeMyLoginRepository
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.InvalidLoginInfo
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.NetworkOk
import org.softwaremaestro.domain.mylogin.entity.Role

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val fakeLoginApi = mockk<LoginApi>(relaxed = true) {
        coEvery { sendRequest(any()) } returns mockk<NetworkResult<LoginResponseDto>>()
    }

    val fakeMyLoginRepository = spyk<FakeMyLoginRepository>(object: FakeMyLoginRepository(fakeLoginApi) {
        override suspend fun autologin(): NetworkResult<EmptyResponseDto> {
            return mockk<NetworkResult<EmptyResponseDto>>(relaxed = true)
        }

        override fun isValid(dto: LoginRequestDto): Boolean {
            return mockk<Boolean>(relaxed = true)
        }
    }, recordPrivateCalls = true)

    context("로그인한다") {
        xtest("로그인 버튼을 누르면 로그인을 시작한다") {

        }

        context("로그인 정보가 유효하지 않으면") {
            every { fakeMyLoginRepository["isValid"](ofType<LoginRequestDto>()) } returns false

            test("로그인 정보가 유효하지 않음 실패를 반환한다") {
                val result = fakeMyLoginRepository.login("", "")
                result should beInstanceOf<InvalidLoginInfo>()
            }
        }

        context("서버가 실패 응답을 반환하면") {
            every { fakeMyLoginRepository["isValid"](ofType<LoginRequestDto>()) } returns true

            val failureMessage = "message"
            val unsuccessfulResult = mockk<NetworkFailure>(relaxed = true) {
                every { message } returns failureMessage
            }

            coEvery { fakeLoginApi.sendRequest(any()) } returns unsuccessfulResult

            test("로그인을 실패 처리한다") {
                val result = fakeMyLoginRepository.login("", "")
                result should beInstanceOf<NetworkFailure>()
            }

            test("로그인 응답은 로그인이 실패한 이유를 포함한다") {
                val result = fakeMyLoginRepository.login("", "") as NetworkFailure
                result.message shouldBe failureMessage
            }

            xtest("사용자에게 로그인이 실패했음을 알린다") {
                fakeMyLoginRepository.login("", "")
            }
        }

        context("서버가 성공 응답을 반환하면") {
            every { fakeMyLoginRepository["isValid"](ofType<LoginRequestDto>()) } returns true

            xcontext("사용자는 홈 화면으로 이동한다") {
                test("학생 회원은 학생 홈 화면으로 이동한다") {
                    val studentDto = mockk<LoginResponseDto>(relaxed = true) {
                        every { role } returns Role.STUDENT
                    }

                    val successfulResult = mockk<NetworkOk<LoginResponseDto>>(relaxed = true) {
                        every { dto } returns studentDto
                    }

                    coEvery { fakeLoginApi.sendRequest(any()) } returns successfulResult

                    fakeMyLoginRepository.login("", "")

                    // 학생 홈 화면으로 이동한다
                }

                test("선생님 회원은 선생님 홈 화면으로 이동한다") {
                    val studentDto = mockk<LoginResponseDto>(relaxed = true) {
                        every { role } returns Role.TEACHER
                    }

                    val successfulResult = mockk<NetworkOk<LoginResponseDto>>(relaxed = true) {
                        every { dto } returns studentDto
                    }

                    coEvery { fakeLoginApi.sendRequest(any()) } returns successfulResult

                    fakeMyLoginRepository.login("", "")
                }
            }
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

    afterEach { unmockkAll() }
})