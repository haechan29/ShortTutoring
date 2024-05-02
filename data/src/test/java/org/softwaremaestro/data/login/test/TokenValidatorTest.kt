package org.softwaremaestro.data.login.test

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.softwaremaestro.data.login.fake.FakeTokenValidator
import org.softwaremaestro.domain.mylogin.entity.LoginToken

class TokenValidatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val validator = FakeTokenValidator

    val token = mockk<LoginToken>()

    context("토큰의 유효성을 검사한다") {
        test("유효한 토큰은 유효하다고 판단한다") {
            every { token.isValid() } returns true

            validator.isValid(token) shouldBe true
        }

        test("유효하지 않은 토큰은 유효하지 않다고 판단한다") {
            every { token.isValid() } returns false

            validator.isValid(token) shouldBe false
        }
    }
})