package org.softwaremaestro.data.login.login_repository

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.beEmpty
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeMyLoginRepository
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenRepository
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RequestDto

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val fakeLoginApi = mockk<LoginApi>(relaxed = true) {
        coEvery { sendRequest(any()) } returns mockk<NetworkResult<LoginResponseDto>>()
    }

    val fakeMyLoginRepository = spyk<FakeMyLoginRepository>(object: FakeMyLoginRepository(fakeLoginApi) {
        override suspend fun autologin(): NetworkResult<Unit> {
            TODO("Not yet implemented")
        }
    }, recordPrivateCalls = true)

    val validId = "id"
    val validPassword = "password"
    val invalidId = ""
    val invalidPassword = ""

    context("로그인한다") {
        xtest("로그인 버튼을 누르면 로그인을 시작한다") {

        }

        context("아이디와 비밀번호를 검증한다") {
            test("유효하지 않은 아이디를 입력하면 로그인 정보가 유효하지 않다는 결과를 반환한다") {
                val result = fakeMyLoginRepository.login(id = invalidId, password = validPassword)
                result should beInstanceOf<Failure>()
            }

            test("유효하지 않은 비밀번호를 입력하면 로그인 정보가 유효하지 않다는 결과를 반환한다") {
                val result = fakeMyLoginRepository.login(id = validId, password = invalidPassword)
                result should beInstanceOf<Failure>()
            }
        }

        context("유효한 아이디와 비밀번호를 입력하면") {
            val result = fakeMyLoginRepository.login(validId, validPassword)

            test("API를 호출한다") {
                coVerify { fakeLoginApi.sendRequest(any()) }
            }

            test("API가 반환한 응답을 반환한다") {
                result shouldBe fakeLoginApi.sendRequest(mockk<RequestDto>())
            }
        }

        context("서버가 실패 응답을 반환하면") {
            val failureMessage = "message"
            val unsuccessfulResult = mockk<Failure>(relaxed = true) {
                every { message } returns failureMessage
            }

            coEvery { fakeLoginApi.sendRequest(any()) } returns unsuccessfulResult

            test("로그인을 실패 처리한다") {
                val result = fakeMyLoginRepository.login(validId, validPassword)
                result should beInstanceOf<Failure>()
            }

            test("로그인 응답은 로그인이 실패한 이유를 포함한다") {
                val result = fakeMyLoginRepository.login(validId, validPassword) as Failure

                result.message shouldNot beEmpty()
            }

            xtest("사용자에게 로그인이 실패했음을 알린다") {
                fakeMyLoginRepository.login(validId, validPassword)
            }
        }

        context("서버가 성공 응답을 반환하면") {
            val validToken = mockk<LoginToken> {
                every { isValid() } returns true
            }

            val successfulResult = mockk<Ok<LoginToken>>(relaxed = true) {
                every { body } returns validToken
            }

//            coEvery { fakeLoginApi.login() } returns successfulResult

            val result = fakeMyLoginRepository.login(validId, validPassword) as Ok<LoginToken>

            xtest("로그인을 성공 처리한다") {
                result should beInstanceOf<Ok<Any>>()
            }

            test("응답은 역할(학생, 선생님)을 포함한다") {
                val body = result.body
//                body.role shouldNotBe null
            }

            xcontext("사용자는 홈 화면으로 이동한다") {
                test("학생 회원은 학생 홈 화면으로 이동한다")

                test("선생님 회원은 선생님 홈 화면으로 이동한다")
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
            coEvery { fakeMyLoginRepository["loadAccessToken"]() } returns spyk<Failure>()

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