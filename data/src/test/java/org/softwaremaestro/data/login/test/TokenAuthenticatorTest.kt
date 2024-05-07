package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeTokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsAuthenticated
import org.softwaremaestro.domain.mylogin.entity.AccessTokenIsNotAuthenticated
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.RefreshTokenIsNotAuthenticated

class TokenAuthenticatorTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    mockkObject(FakeAccessTokenRepository, recordPrivateCalls = true)
    mockkObject(FakeRefreshTokenRepository, recordPrivateCalls = true)

    mockkObject(FakeTokenAuthenticator)

    context("액세스 토큰과 리프레시 토큰을 검증한다") {
        test("액세스 토큰을 검증한다") {
            FakeTokenAuthenticator.authToken()

            coVerify { FakeAccessTokenRepository.load() }
        }

        test("액세스 토큰이 검증을 통과하면 액세스 토큰이 검증되었다는 결과를 반환한다") {
            coEvery { FakeAccessTokenRepository.load() } returns mockk<Ok<LoginAccessToken>>()

            FakeTokenAuthenticator.authToken() shouldBe AccessTokenIsAuthenticated
        }


        context("액세스 토큰이 검증을 통과하지 못하면") {
            coEvery { FakeAccessTokenRepository.load() } returns mockk<Failure>()

            test("못하면 리프레시 토큰을 검증한다") {
                FakeTokenAuthenticator.authToken()

                coVerify { FakeRefreshTokenRepository.load() }
            }

            context("리프레시 토큰이 검증을 통과하면") {
                coEvery { FakeRefreshTokenRepository.load() } returns mockk<Ok<LoginRefreshToken>>()

                test("액세스 토큰이 검증에 실패했다는 결과를 반환한다") {
                    val result = FakeTokenAuthenticator.authToken()

                    result shouldBe AccessTokenIsNotAuthenticated
                }
            }

            context("리프레시 토큰이 검증을 통과하지 못하면") {
                coEvery { FakeRefreshTokenRepository.load() } returns mockk<Failure>()

                test("리프레시 토큰이 검증에 실패했다는 결과를 반환한다") {
                    val result = FakeTokenAuthenticator.authToken()

                    result shouldBe RefreshTokenIsNotAuthenticated
                }
            }
        }
    }

    afterEach { unmockkAll() }
})