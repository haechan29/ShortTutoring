package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeTokenAuthenticator
import org.softwaremaestro.domain.mylogin.TokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated

class TokenAuthenticatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val accessTokenRepository = mockk<TokenRepository<LoginAccessToken>>(relaxed = true)
    val refreshTokenRepository = mockk<TokenRepository<LoginRefreshToken>>(relaxed = true)

    val tokenAuthenticator = spyk(
        object: FakeTokenAuthenticator(accessTokenRepository, refreshTokenRepository) {},
        recordPrivateCalls = true
    )

    context("액세스 토큰과 리프레시 토큰을 검증한다") {
        test("액세스 토큰을 검증한다") {
            tokenAuthenticator.authToken()

            coVerify { accessTokenRepository.load() }
        }

        test("액세스 토큰이 검증을 통과하면 액세스 토큰이 검증되었다는 결과를 반환한다") {
            coEvery { accessTokenRepository.load() } returns mockk<Ok<LocalTokenResponseDto>>()

            tokenAuthenticator.authToken() shouldBe AccessTokenIsAuthenticated
        }


        context("액세스 토큰이 검증을 통과하지 못하면") {
            coEvery { accessTokenRepository.load() } returns mockk<Failure>()

            test("못하면 리프레시 토큰을 검증한다") {
                tokenAuthenticator.authToken()

                coVerify { refreshTokenRepository.load() }
            }

            context("리프레시 토큰이 검증을 통과하면") {
                coEvery { refreshTokenRepository.load() } returns mockk<Ok<LocalTokenResponseDto>>()

                test("액세스 토큰이 검증에 실패했다는 결과를 반환한다") {
                    val result = tokenAuthenticator.authToken()

                    result shouldBe AccessTokenIsNotAuthenticated
                }
            }

            context("리프레시 토큰이 검증을 통과하지 못하면") {
                coEvery { refreshTokenRepository.load() } returns mockk<Failure>()

                test("리프레시 토큰이 검증에 실패했다는 결과를 반환한다") {
                    val result = tokenAuthenticator.authToken()

                    result shouldBe RefreshTokenIsNotAuthenticated
                }
            }
        }
    }

    afterEach { unmockkAll() }
})