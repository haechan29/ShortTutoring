package org.softwaremaestro.data.login

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.softwaremaestro.domain.login.MyLoginRepository
import org.softwaremaestro.domain.login.entity.InvalidTokenException
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.domain.login.entity.TokenNotFoundException

class TokenValidatorTest: FunSpec({
    val validator = FakeTokenValidator

    context("토큰의 유효성을 검사한다") {
        test("유효한 토큰을 검사하면 InvalidTokenException을 발생시키지 않는다") {
            val validToken = LoginToken("") { true }

            shouldNotThrow<InvalidTokenException> {
                validator.validate(validToken)
            }
        }

        test("유효하지 않은 토큰을 검사하면 InvalidTokenException을 발생시킨다") {
            val invalidToken = LoginToken("") { false }

            shouldThrow<InvalidTokenException> {
                validator.validate(invalidToken)
            }
        }
    }
})