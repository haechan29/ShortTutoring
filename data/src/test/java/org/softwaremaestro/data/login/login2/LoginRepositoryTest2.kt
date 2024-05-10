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
    xcontext("API가 서버로 요청을 전송할때 토큰을 추가한다") {}

    xcontext("토큰의 유효성을 주기적으로 확인한다") {}


    // 토큰 추가
    xcontext("토큰을 검증한다") {}

    xcontext("액세스 토큰이 유효하지 않으면 액세스 토큰을 발급받는다") {}

    xcontext("리프레시 토큰이 유효하지 않으면 리프레시 토큰을 발급받는다") {}

    xcontext("토큰 발급이 진행되는 동안 토큰 발급을 재시작하지 않는다")

    xcontext("토큰 발급을 시도했을 때 이미 발급 중이면 요청을 저장했다가 일괄 처리한다")

    xcontext("토큰 발급이 실패하면 요청을 모두 폐기한다")

    xcontext("토큰 발급이 성공하면 발급된 토큰을 반환한다")

    // 별도
    xcontext("토큰의 유효성을 주기적으로 확인한다")
})