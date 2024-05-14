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
import org.softwaremaestro.data.mylogin.fake.FakeTokenInjector
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.result.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.result.AuthFailure
import org.softwaremaestro.domain.mylogin.entity.result.AuthSuccess
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.Request
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.result.RefreshTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenIssuer
import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto

class TokenInjectorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val tokenAuthenticator = mockk<TokenAuthenticator>(relaxed = true)
    val accessTokenRepository = mockk<TokenRepository<LoginAccessToken>>(relaxed = true)
    val accessTokenIssuer = mockk<TokenIssuer<LoginAccessToken>>(relaxed = true)
    val refreshTokenIssuer = mockk<TokenIssuer<LoginRefreshToken>>(relaxed = true)

    val tokenInjector = spyk(
        objToCopy = object: FakeTokenInjector(
            tokenAuthenticator,
            accessTokenRepository,
            accessTokenIssuer,
            refreshTokenIssuer
        ) {},
        recordPrivateCalls = true
    ) {
        coEvery { this@spyk["authenticateToken"]() } returns mockk<AuthSuccess>(relaxed = true)
        coEvery { this@spyk["issueAccessToken"]() } returns mockk<NetworkResult<EmptyResponseDto>>(relaxed = true)
        coEvery { this@spyk["issueRefreshToken"]() } returns mockk<NetworkResult<EmptyResponseDto>>(relaxed = true)
        coEvery { this@spyk["addTokenToRequestHeader"](ofType<LoginAccessToken>()) } returns mockk<NetworkResult<EmptyResponseDto>>(relaxed = true)
    }

    context("액세스 토큰이 유효하지 않으면") {
        coEvery { tokenInjector["authenticateToken"]() } returns AccessTokenIsNotAuthenticated

        tokenInjector.injectToken(mockk<Request<RequestDto>>(relaxed = true))

        test("액세스 토큰을 발급받는다") {
            coVerify { tokenInjector["issueAccessToken"]() }
        }
    }

    context("리프레시 토큰이 유효하지 않으면") {
        coEvery { tokenInjector["authenticateToken"]() } returns RefreshTokenIsNotAuthenticated

        tokenInjector.injectToken(mockk<Request<RequestDto>>(relaxed = true))

        test("리프레시 토큰을 발급받는다") {
            coVerify { tokenInjector["issueRefreshToken"]() }
        }
    }

    context("토큰 발급이 실패하면") {
        coEvery { tokenInjector["checkTokenOrFail"]() } returns mockk<NetworkFailure>(relaxed = true)

        val result = tokenInjector.injectToken(mockk<Request<RequestDto>>(relaxed = true))

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
                    tokenInjector.injectToken(mockk<Request<RequestDto>>(relaxed = true))
                }
            }
        }

        test("토큰 발급을 재시작하지 않는다") {
            coVerify(exactly = 1) { tokenInjector["issueTokenFromServer"](ofType<AuthFailure>()) }
        }
    }
})