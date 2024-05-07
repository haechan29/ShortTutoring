package org.softwaremaestro.data.login.test

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
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val fakeLoginApi = mockk<LoginApi>(relaxed = true) {
        coEvery { login() } returns mockk<NetworkResult<LoginToken>>()
    }

    val fakeMyLoginRepository = spyk<FakeMyLoginRepository>(recordPrivateCalls = true) {
        every { api } returns fakeLoginApi
    }

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
                coVerify { fakeLoginApi.login() }
            }

            test("API가 반환한 응답을 반환한다") {
                result shouldBe fakeLoginApi.login()
            }
        }

        context("서버가 실패 응답을 반환하면") {
            val failureMessage = "message"
            val unsuccessfulResult = mockk<Failure>(relaxed = true) {
                every { message } returns failureMessage
            }

            coEvery { fakeLoginApi.login() } returns unsuccessfulResult

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
            test("로그인을 성공 처리한다") {
                val validToken = mockk<LoginToken> {
                    every { isValid() } returns true
                }

                val successfulResult = mockk<Ok<LoginToken>>(relaxed = true) {
                    every { body } returns validToken
                }

                coEvery { fakeLoginApi.login() } returns successfulResult

                val result = fakeMyLoginRepository.login(validId, validPassword) as Ok<LoginToken>

                result should beInstanceOf<Ok<Any>>()
            }

            context("토큰을 포함하면") {
                val accessTokenResult = mockk<Ok<LoginToken>>(relaxed = true) {
                    every { body } returns mockk<LoginToken>()
                }

                coEvery { fakeLoginApi.login() } returns accessTokenResult

                coEvery { fakeMyLoginRepository["saveToken"](ofType<Ok<Any>>()) } returns mockk<Ok<LoginToken>>()

                fakeMyLoginRepository.login(validId, validPassword)

                test("토큰을 저장한다") {
                    coVerify { fakeMyLoginRepository["saveToken"](ofType<Ok<Any>>()) }
                }
            }

            xtest("사용자는 홈 화면으로 이동한다") {

            }
        }
    }

    context("자동 로그인한다") {
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

    afterEach { unmockkAll() }
})