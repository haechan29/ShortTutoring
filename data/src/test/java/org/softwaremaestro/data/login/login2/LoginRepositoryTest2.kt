package org.softwaremaestro.data.login.login2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk
import org.softwaremaestro.data.login.fake.FakeMyLoginRepositoryImpl
import org.softwaremaestro.domain.mylogin.entity.TokenManager
import org.softwaremaestro.domain.mylogin.entity.TokenStorage
import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.Api

class LoginRepositoryTest2: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val storage = mockk<TokenStorage>(relaxed = true)
    val validator = mockk<TokenValidator>(relaxed = true)
    val api = mockk<Api>(relaxed = true)
    val tokenManager = mockk<TokenManager>(relaxed = true)
    val repository = FakeMyLoginRepositoryImpl(storage, validator, api, tokenManager)

    context("자동 로그인한다") {
        test("Splash Activity에 진입하면 자동 로그인을 시작한다") {
            // TODO()
        }

        context("자동 로그인한다") {
            repository.autologin()

            test("자동 로그인이 시작되면 액세스 토큰 인증을 시작한다") {
                coVerify { tokenManager.authAccessToken() }
            }
        }

        xcontext("실행하지 않을 테스트") {

            test("유효한 액세스 토큰을 가지고 있다면 서버에 전송한다")

            test("유효하지 않은 액세스 토큰을 가지고 있다면 InvalidAccessTokenException을 발생시킨다")

            test("리프레시 토큰 인증을 시작하면 리프레시 토큰을 가지고 있는지 확인한다")

            test("리프레시 토큰을 가지고 있다면 유효성을 확인한다")

            test("유효한 리프레시 토큰을 가지고 있다면 서버에 전송한다")

            test("유효하지 않은 리프레시 토큰을 가지고 있다면 리프레시 토큰 인증을 실패 처리한다")

            test("리프레시 토큰을 가지고 있지 않다면 리프레시 토큰 인증을 실패 처리한다")

            test("리프레시 토큰 인증을 실패하면 이유를 밝힌다")
        }
    }

    xcontext("요청을 전송한다") {
        test("요청을 전송하기 전에 토큰의 유효성을 확인한다")

        test("요청을 전송할 때 액세스 토큰을 요청 헤더에 삽입한다")
    }

    xcontext("응답을 처리한다") {
        context("액세스 토큰 인증을 시도했을 때 응답을 처리한다") {
            test("액세스 토큰이 유효하다는 응답을 받으면 InvalidAccessTokenException을 발생시키지 않는다")

            test("액세스 토큰이 유효하지 않다는 응답을 받으면 InvalidAccessTokenException을 발생시킨다")
        }

        context("리프레시 토큰 인증을 시도했을 때 응답을 처리한다") {
            test("리프레시 토큰이 유효하다는 응답을 받으면 InvalidRefreshTokenException을 발생시키지 않는다")

            test("리프레시 토큰이 유효하다는 응답을 받으면 응답 바디는 액세스 토큰을 포함한다")

            test("리프레시 토큰이 유효하다는 응답을 받으면 액세스 토큰을 저장한다")

            test("리프레시 토큰이 유효하지 않다는 응답을 받으면 InvalidRefreshTokenException을 발생시킨다")
        }

        context("사용자 인증을 시도했을 때 응답을 처리한다") {
            test("사용자 인증이 실패하면 UnauthorizedUserException을 발생시킨다")

            test("사용자 인증에 성공하면 응답 바디는 역할(학생, 선생님)을 포함한다")

            test("사용자 인증에 성공하면 액세스 토큰을 저장한다")

            test("사용자 인증에 성공하면 리프레시 토큰을 저장한다")

            test("학생 회원이 사용자 인증에 성공하면 학생 홈 화면으로 이동한다")

            test("학생 회원이 사용자 인증에 성공했을 때만 학생 홈 화면으로 이동한다")

            test("선생님 회원이 사용자 인증에 성공하면 선생님 홈 화면으로 이동한다")

            test("선생님 회원이 사용자 인증에 성공했을 때만 선생님 홈 화면으로 이동한다")
        }
    }

    xcontext("토큰이 재발급되면 로그인을 시도한다") {
        test("액세스 토큰이 재발급되면 로그인을 시도한다")

        test("리프레시 토큰이 재발급되면 로그인을 시도한다")

        test("토큰이 빠른 시간 안에 반복적으로 발급되어도 로그인을 여러 번 시도하지 않는다")

        test("InvalidRefreshTokenException이 발생하면 사용자 로그인을 시작한다")

        test("UnauthorizedUserException이 발생하면 로그인이 실패했음을 알린다")
    }

    xcontext("동시적인 토큰 발급 요청을 처리한다") {
        context("동시적인 액세스 토큰 발급 요청을 처리한다") {
            test("액세스 토큰은 짧은 시간 안에 반복적으로 발급되지 않는다")

            test("리프레시 토큰 인증이 시작되면 리프레시 토큰 인증이 진행 중임을 표시한다")

            test("리프레시 토큰 인증이 진행 중이면 인증이 재시작되지 않는다")

            test("리프레시 토큰 인증이 진행 중이면 요청을 저장한다")

            test("리프레시 토큰 인증이 종료되면 리프레시 토큰 인증이 진행 중이 아님을 표시한다")

            test("리프레시 토큰 인증이 성공하면 기존의 요청들을 일괄적으로 처리한다")

            test("리프레시 토큰 인증이 실패하면 기존의 요청들을 일괄적으로 폐기한다")
        }

        context("동시적인 리프레시 토큰 발급 요청을 처리한다") {
            test("리프레시 토큰은 짧은 시간 안에 반복적으로 발급되지 않는다")

            test("사용자 로그인이 시작되면 사용자 로그인이 진행 중임을 표시한다")

            test("사용자 로그인을 진행하는 동안 사용자 로그인이 재시작되지 않는다")

            test("사용자 로그인이 종료되면 사용자 로그인이 진행 중이 아님을 표시한다")
        }
    }

    xcontext("토큰의 유효성을 주기적으로 확인한다")
})