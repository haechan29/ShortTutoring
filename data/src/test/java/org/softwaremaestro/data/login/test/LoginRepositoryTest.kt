package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beOfType
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.mylogin.fake.FakeMyLoginRepositoryImpl
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Ok

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("로그인한다") {
        val tokenRepository = mockk<TokenRepository<String>>(relaxed = true)
        val api = spyk<Api>()
        val myLoginRepository = FakeMyLoginRepositoryImpl(api, tokenRepository)

        val validId = "id"
        val validPassword = "password"
        val invalidId = ""
        val invalidPassword = ""

        context("아이디와 비밀번호를 검증한다") {
            test("유효하지 않은 아이디를 입력하면 로그인 정보가 유효하지 않다는 결과를 반환한다") {
                val result = myLoginRepository.login(id = invalidId, password = validPassword)
                result shouldBe Failure
            }

            test("유효하지 않은 비밀번호를 입력하면 로그인 정보가 유효하지 않다는 결과를 반환한다") {
                val result = myLoginRepository.login(id = validId, password = invalidPassword)
                result shouldBe Failure
            }
        }

        test("유효한 아이디와 비밀번호를 입력하면 API를 호출한다") {
            myLoginRepository.login(validId, validPassword)

            coVerify { api.send(any()) }
        }

        test("유효한 아이디와 비밀번호를 입력하면 로그인이 성공했다는 결과를 반환한다") {
            myLoginRepository.login(validId, validPassword) should beOfType<Ok<Any>>()
        }

        test("로그인이 성공하면 로그인 응답을 얻는다") {
            val result = myLoginRepository.login(validId, validPassword) as Ok<Any>
            result.body shouldNotBe null
        }
    }
})