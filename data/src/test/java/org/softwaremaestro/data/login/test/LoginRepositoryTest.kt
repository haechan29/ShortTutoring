package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.mylogin.fake.FakeMyLoginRepositoryImpl
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.LoginResult
import org.softwaremaestro.domain.mylogin.entity.Api

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("로그인한다") {
        val tokenRepository = mockk<TokenRepository>(relaxed = true)

        val validId = "id"
        val validPassword = "password"
        val invalidId = ""
        val invalidPassword = ""

        context("아이디와 비밀번호를 검증한다") {
            val api = mockk<Api>(relaxed = true)
            val myLoginRepository = FakeMyLoginRepositoryImpl(api, tokenRepository)

            test("유효하지 않은 아이디를 입력하면 로그인 정보가 유효하지 않다는 결과를 반환한다") {
                val result = myLoginRepository.login(id = invalidId, password = validPassword)
                result shouldBe LoginResult.INVALID_LOGIN_INFO
            }

            test("유효하지 않은 비밀번호를 입력하면 로그인 정보가 유효하지 않다는 결과를 반환한다") {
                val result = myLoginRepository.login(id = validId, password = invalidPassword)
                result shouldBe LoginResult.INVALID_LOGIN_INFO
            }
        }

        test("유효한 아이디와 비밀번호를 입력하면 API를 호출한다") {
            val api = spyk<Api>()
            val myLoginRepository = FakeMyLoginRepositoryImpl(api, tokenRepository)

            myLoginRepository.login(validId, validPassword)

            coVerify { api.send(any()) }
        }

        test("유효한 아이디와 비밀번호를 입력하면 로그인이 성공했다는 결과를 반환한다") {
            val api = mockk<Api>(relaxed = true)
            val myLoginRepository = FakeMyLoginRepositoryImpl(api, tokenRepository)

            myLoginRepository.login(validId, validPassword) shouldBe LoginResult.OK
        }

        xtest("로그인이 성공하면 로그인 응답을 얻는다") {

        }
    }
})