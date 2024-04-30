package org.softwaremaestro.data.login.test

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import org.softwaremaestro.data.login.fake.FakeTokenValidator
import org.softwaremaestro.domain.mylogin.entity.FakeLoginToken
import org.softwaremaestro.domain.mylogin.exception.InvalidTokenException
import org.softwaremaestro.domain.mylogin.entity.LoginToken

class TokenValidatorTest: FunSpec({
    val validator = FakeTokenValidator

    context("토큰의 유효성을 검사한다") {
        test("유효한 토큰을 검사하면 InvalidTokenException을 발생시키지 않는다") {
            val validToken = FakeLoginToken("") { true }

            shouldNotThrow<InvalidTokenException> {
                FakeTokenValidator.validate(validToken)
            }
        }

        test("유효하지 않은 토큰을 검사하면 InvalidTokenException을 발생시킨다") {
            val invalidToken = FakeLoginToken("") { false }

            shouldThrow<InvalidTokenException> {
                FakeTokenValidator.validate(invalidToken)
            }
        }
    }
})