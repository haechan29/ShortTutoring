package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import org.softwaremaestro.data.login.fake.FakeMyLoginRepositoryImpl
import org.softwaremaestro.data.login.fake.FakeTokenStorage
import org.softwaremaestro.data.login.fake.FakeTokenValidator
import org.softwaremaestro.domain.login.entity.LoginToken

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val storage = spyk(FakeTokenStorage)
    val validator = spyk(FakeTokenValidator)
    val repository = FakeMyLoginRepositoryImpl(storage, validator)

    val token = LoginToken("") { true }

    context("토큰을 저장한다") {
        beforeEach {
            repository.save(token)
        }

        test("토큰을 저장할 때 유효성을 검사한다") {
            verify(exactly = 1) { validator["validate"](token) }
        }

        test("토큰을 저장할 때 TokenStorage에 저장한다") {
            verify(exactly = 1) { storage["save"](token) }
        }

        test("토큰을 저장할 때 유효성을 검사한 후에 TokenStorage에 저장한다") {
            verifyOrder {
                validator["validate"](token)
                storage["save"](token)
            }
        }
    }

    context("토큰을 로드한다") {
        beforeEach {
            FakeTokenStorage.save(token)
            repository.load()
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한다") {
            verify(exactly = 1) { storage["load"]() }
        }

        test("토큰을 로드할 때 유효성을 검사한다") {
            verify(exactly = 1) { validator["validate"](token) }
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한 후에 유효성을 검사한다") {
            verifyOrder {
                storage["load"]()
                validator["validate"](token)
            }
        }
    }

    context("자동 로그인한다") {
        test("Splash Activity에 진입하면 자동 로그인을 시작한다")

        test("자동 로그인이 시작되면 액세스 토큰 인증을 시작한다")

        test("액세스 토큰 인증을 시작하면 액세스 토큰을 가지고 있는지 확인한다")

        test("액세스 토큰을 가지고 있다면 유효성을 확인한다")

        test("유효한 액세스 토큰을 가지고 있다면 서버에 전송한다")

        test("유효하지 않은 액세스 토큰을 가지고 있다면 InvalidAccessTokenException을 발생시킨다")

        test("액세스 토큰을 가지고 있지 않다면 InvalidAccessTokenException을 발생시킨다")

        test("리프레시 토큰 인증을 시작하면 리프레시 토큰을 가지고 있는지 확인한다")

        test("리프레시 토큰을 가지고 있다면 유효성을 확인한다")

        test("유효한 리프레시 토큰을 가지고 있다면 서버에 전송한다")

        test("유효하지 않은 리프레시 토큰을 가지고 있다면 InvalidRefreshTokenException을 발생시킨다")

        test("리프레시 토큰을 가지고 있지 않다면 InvalidRefreshTokenException을 발생시킨다")
    }

    context("로그인한다") {
        test("아이디를 입력하지 않으면 로그인을 진행할 수 없다")

        test("비밀번호를 입력하지 않으면 로그인을 진행할 수 없다")

        test("로그인하면 로그인 정보를 서버에 전송한다")
    }

    context("요청을 전송한다") {
        test("요청을 전송하기 전에 토큰의 유효성을 확인한다")

        test("요청을 전송할 때 액세스 토큰을 요청 헤더에 삽입한다")
    }

    context("응답을 처리한다") {
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

    context("토큰 관련 예외를 처리한다") {
        test("InvalidAccessTokenException이 발생하면 리프레시 토큰 인증을 시작한다")

        test("InvalidRefreshTokenException이 발생하면 사용자 로그인을 시작한다")

        test("UnauthorizedUserException이 발생하면 로그인이 실패했음을 알린다")
    }

    context("동시적인 토큰 발급 요청을 처리한다") {
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

    context("토큰의 유효성을 주기적으로 확인한다")
})