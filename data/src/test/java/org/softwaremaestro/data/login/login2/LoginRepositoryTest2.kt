package org.softwaremaestro.data.login.login2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.softwaremaestro.data.mylogin.fake.FakeMyLoginRepository
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.LoginApi
import org.softwaremaestro.domain.mylogin.entity.LoginToken

class LoginRepositoryTest2: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val mockApi = mockk<LoginApi>(relaxed = true)
    val mockTokenRepository = mockk<TokenRepository<LoginToken>>(relaxed = true)
    val fakeMyLoginRepository = mockk<FakeMyLoginRepository>(relaxed = true) {
        every { mockApi } returns mockApi
        every { mockTokenRepository } returns mockTokenRepository
    }

    xcontext("응답을 처리한다") {
        context("사용자 인증 응답을 처리한다") {
            test("사용자 인증이 실패하면 사용자에게 알린다")

            test("사용자 인증에 성공하면 응답 바디는 역할(학생, 선생님)을 포함한다")

            test("사용자 인증에 성공하면 액세스 토큰을 저장한다")

            test("사용자 인증에 성공하면 리프레시 토큰을 저장한다")

            test("학생 회원이 사용자 인증에 성공하면 학생 홈 화면으로 이동한다")

            test("선생님 회원이 사용자 인증에 성공하면 선생님 홈 화면으로 이동한다")
        }
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