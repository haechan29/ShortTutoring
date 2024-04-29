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

class TokenStorageTest: FunSpec({
    val tokenStorage = FakeTokenStorage

    beforeEach {
        FakeTokenStorage.clear()
    }

    context("토큰을 저장하고 로드한다") {
        test("토큰을 저장하지 않고 로드하면 TokenNotFoundException이 발생한다") {
            shouldThrow<TokenNotFoundException> {
                "${tokenStorage.load()}"
            }
        }

        test("저장된 토큰을 로드하면 컨텐츠가 유지된다") {
            val token = LoginToken("content") { true }
            tokenStorage.save(token)
            tokenStorage.load().content shouldBe "content"
        }
    }
})