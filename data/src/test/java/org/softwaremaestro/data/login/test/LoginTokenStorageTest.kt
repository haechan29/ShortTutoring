package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.data.fake_login.impl.FakeLoginTokenStorage

class LoginTokenStorageTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val tokenStorage = spyk<FakeLoginTokenStorage>(recordPrivateCalls = true)

    test("로드할 토큰이 존재하지 않으면 null을 반환한다") {
        tokenStorage.clear()

        tokenStorage.load() shouldBe null
    }

    test("토큰을 저장하고 로드했을 때 내용을 유지한다") {
        val token = mockk<LoginToken>(relaxed = true)

        tokenStorage.save(token)

        tokenStorage.load() shouldBe token
    }
})