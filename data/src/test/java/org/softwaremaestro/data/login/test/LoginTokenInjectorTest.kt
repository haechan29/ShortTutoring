package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.softwaremaestro.data.fake_login.fake.FakeTokenInjector
import org.softwaremaestro.domain.fake_login.result.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.fake_login.result.AuthFailure
import org.softwaremaestro.domain.fake_login.result.AuthSuccess
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.data.fake_login.legacy.Request
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.data.fake_login.dto.RequestDto
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.data.fake_login.dto.EmptyResponseDto
import org.softwaremaestro.data.fake_login.legacy.LoginTokenRepositoryImpl
import org.softwaremaestro.domain.fake_login.AccessTokenDao

class LoginTokenInjectorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val accessTokenDao = mockk<AccessTokenDao>(relaxed = true)
    val loginTokenRepository = mockk<LoginTokenRepositoryImpl>(relaxed = true)

    val tokenInjector = spyk(
        objToCopy = FakeTokenInjector(accessTokenDao, loginTokenRepository),
        recordPrivateCalls = true
    ) {
        coEvery { this@spyk["authenticateToken"]() } returns mockk<AuthSuccess>(relaxed = true)
        coEvery { this@spyk["issueAccessToken"]() } returns mockk<NetworkResult<EmptyResponseDto>>(relaxed = true)
        coEvery { this@spyk["issueRefreshToken"]() } returns mockk<NetworkResult<EmptyResponseDto>>(relaxed = true)
        coEvery { this@spyk["addAccessTokenToRequestHeader"](ofType<Request<RequestDto>>(), ofType<LoginAccessToken>()) } returns mockk<Unit>(relaxed = true)
    }

    context("액세스 토큰이 유효하지 않으면") {
        coEvery { tokenInjector["authenticateToken"]() } returns AccessTokenIsNotAuthenticated

        tokenInjector.injectLoginToken(mockk<Request<RequestDto>>(relaxed = true))

        test("액세스 토큰을 발급받는다") {
            coVerify { tokenInjector["issueAccessToken"]() }
        }
    }

    context("리프레시 토큰이 유효하지 않으면") {
        coEvery { tokenInjector["authenticateToken"]() } returns RefreshTokenIsNotAuthenticated

        tokenInjector.injectLoginToken(mockk<Request<RequestDto>>(relaxed = true))

        test("리프레시 토큰을 발급받는다") {
            coVerify { tokenInjector["issueRefreshToken"]() }
        }
    }

    context("토큰 발급이 실패하면") {
        coEvery { tokenInjector["checkTokenOrFail"]() } returns mockk<NetworkFailure>(relaxed = true)

        val result = tokenInjector.injectLoginToken(mockk<Request<RequestDto>>(relaxed = true))

        test("토큰 추가를 실패 처리한다") {
            result should beInstanceOf<NetworkFailure>()
        }
    }

    context("토큰 발급이 진행되고 있다면") {
        coEvery { tokenInjector["authenticateToken"]() } returns mockk<AuthFailure>(relaxed = true)
        coEvery { tokenInjector["issueTokenFromServer"](ofType<AuthFailure>()) } coAnswers {
            delay(100)
            mockk<NetworkResult<EmptyResponseDto>>(relaxed = true)
        }

        runTest {
            repeat(10) {
                launch {
                    tokenInjector.injectLoginToken(mockk<Request<RequestDto>>(relaxed = true))
                }
            }
        }

        test("토큰 발급을 재시작하지 않는다") {
            coVerify(exactly = 1) { tokenInjector["issueTokenFromServer"](ofType<AuthFailure>()) }
        }
    }
})