package org.softwaremaestro.data.login

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.softwaremaestro.domain.login.MyLoginRepository
import org.softwaremaestro.domain.login.entity.InvalidTokenException
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.domain.login.entity.TokenNotFoundException

class LoginRepositoryTest: FunSpec({
    lateinit var repository: MyLoginRepository

    beforeEach {
        repository = FakeMyLoginRepositoryImpl
    }

    context("토큰을 저장하거나 로드할 때 유효성을 검사한다") {
        test("유효하지 않은 토큰을 저장하면 InvalidTokenException이 발생한다") {
            val invalidToken = LoginToken("") { false }

            shouldThrow<InvalidTokenException> {
                repository.save(invalidToken)
            }
        }

        test("로드한 토큰이 유효하지 않으면 InvalidTokenException이 발생한다") {
            val token = mockk<LoginToken>()
            every { token.isValid() } returns true

            repository.save(token)

            every { token.isValid() } returns false

            shouldThrow<InvalidTokenException> {
                repository.load()
            }
        }
    }
})