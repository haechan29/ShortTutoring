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

    // 토큰 인터셉트

    xcontext("토큰의 유효성을 주기적으로 확인한다") {}
})