package org.softwaremaestro.data.login.test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.softwaremaestro.data.login.fake.FakeTokenStorage
import org.softwaremaestro.domain.mylogin.entity.LoginToken

class TokenStorageTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val tokenStorage = FakeTokenStorage

    context("토큰을 저장하고 로드한다") {
        test("로드할 토큰이 존재하지 않으면 null을 반환한다") {
            tokenStorage.load() shouldBe null
        }

        test("토큰을 저장하고 로드했을 때 내용을 유지한다") {
            val mContent = "content"

            val token = mockk<LoginToken> {
                every { content } returns mContent
            }

            tokenStorage.save(token)
            val newToken = tokenStorage.load()

            mContent shouldBe newToken?.content
        }
    }
})