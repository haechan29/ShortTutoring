package org.softwaremaestro.data.login.login2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.beIn
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.beOfType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType
import org.junit.internal.runners.statements.Fail
import org.softwaremaestro.data.mylogin.fake.FakeTokenRepository
import org.softwaremaestro.domain.mylogin.entity.AccessTokenNotFound
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.AttemptResult
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.ACCESS_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_ACCESS_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_REFRESH_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.REFRESH_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

class TokenRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("액세스 토큰 인증을 진행한다") {
        val tokenStorage = mockk<TokenStorage>(relaxed = true)
        val tokenRepository = spyk(FakeTokenRepository(tokenStorage), recordPrivateCalls = true)

        test("액세스 토큰 인증을 시작하면 저장된 액세스 토큰을 로드한다") {
            tokenRepository.authAccessToken()

            coVerify { tokenRepository["readAccessToken"]() }
        }

        test("액세스 토큰을 가지고 있지 않다면 액세스 토큰 인증을 실패 처리한다") {
            coEvery { tokenRepository["readAccessToken"]() } returns null

            tokenRepository.authAccessToken() should beInstanceOf<Failure>()
        }

        test("액세스 토큰을 가지고 있지 않아 액세스 토큰 인증이 실패하면 이를 알린다") {
            coEvery { tokenRepository["readAccessToken"]() } returns null

            val result = tokenRepository.authAccessToken() as Failure
            result.message shouldBe ACCESS_TOKEN_NOT_FOUND
        }

        test("액세스 토큰을 가지고 있다면 유효성을 확인한다") {
            val token = spyk(LoginAccessToken(""))

            coEvery { tokenRepository["readAccessToken"]() } returns token

            tokenRepository.authAccessToken()

            verify { token.isValid() }
        }

        test("유효하지 않은 액세스 토큰을 가지고 있다면 액세스 토큰 인증을 실패 처리한다") {
            val invalidToken = mockk<LoginAccessToken> {
                every { isValid() } returns false
            }

            coEvery { tokenRepository["readAccessToken"]() } returns invalidToken

            tokenRepository.authAccessToken() should beInstanceOf<Failure>()
        }

        test("액세스 토큰이 유효하지 않아 액세스 토큰 인증이 실패하면 이를 알린다") {
            val invalidToken = mockk<LoginAccessToken> {
                every { isValid() } returns false
            }

            coEvery { tokenRepository["readAccessToken"]() } returns invalidToken

            val result = tokenRepository.authAccessToken() as Failure
            result.message shouldBe INVALID_ACCESS_TOKEN
        }

        test("유효한 액세스 토큰을 가지고 있다면 액세스 토큰 인증을 성공 처리한다") {
            val validToken = mockk<LoginAccessToken> {
                every { isValid() } returns true
            }

            coEvery { tokenRepository["readAccessToken"]() } returns validToken

            tokenRepository.authAccessToken() should beInstanceOf<Ok<Any>>()
        }
    }

    context("리프레시 토큰 인증을 진행한다") {
        val tokenStorage = mockk<TokenStorage>(relaxed = true)
        val tokenRepository = spyk(FakeTokenRepository(tokenStorage), recordPrivateCalls = true)

        test("리프레시 토큰 인증을 시작하면 리프레시 토큰을 가지고 있는지 확인한다") {
            tokenRepository.authRefreshToken()

            coVerify { tokenRepository["readRefreshToken"]() }
        }

        test("리프레시 토큰을 가지고 있지 않다면 리프레시 토큰 인증을 실패 처리한다") {
            coEvery { tokenRepository["readRefreshToken"]() } returns null

            tokenRepository.authRefreshToken() should beInstanceOf<Failure>()
        }

        test("리프레시 토큰을 가지고 있지 않아 리프레시 토큰 인증이 실패하면 이를 알린다") {
            coEvery { tokenRepository["readRefreshToken"]() } returns null

            val result = tokenRepository.authRefreshToken() as Failure
            result.message shouldBe REFRESH_TOKEN_NOT_FOUND
        }

        test("리프레시 토큰을 가지고 있다면 유효성을 확인한다") {
            val token = spyk(LoginRefreshToken(""))

            coEvery { tokenRepository["readRefreshToken"]() } returns token

            tokenRepository.authRefreshToken()

            verify { token.isValid() }
        }

        test("유효하지 않은 리프레시 토큰을 가지고 있다면 리프레시 토큰 인증을 실패 처리한다") {
            val invalidToken = mockk<LoginRefreshToken> {
                every { isValid() } returns false
            }

            coEvery { tokenRepository["readRefreshToken"]() } returns invalidToken

            tokenRepository.authRefreshToken() should beInstanceOf<Failure>()
        }

        test("리프레시 토큰이 유효하지 않아 리프레시 토큰 인증이 실패하면 이를 알린다") {
            val invalidToken = mockk<LoginRefreshToken> {
                every { isValid() } returns false
            }

            coEvery { tokenRepository["readRefreshToken"]() } returns invalidToken

            val result = tokenRepository.authRefreshToken() as Failure
            result.message shouldBe INVALID_REFRESH_TOKEN
        }

        test("유효한 리프레시 토큰을 가지고 있다면 리프레시 토큰 인증을 성공 처리한다") {
            val validToken = mockk<LoginRefreshToken> {
                every { isValid() } returns true
            }

            coEvery { tokenRepository["readRefreshToken"]() } returns validToken

            tokenRepository.authRefreshToken() should beInstanceOf<Ok<Any>>()
        }
    }

    context("토큰을 저장한다") {
        val storage = mockk<TokenStorage>(relaxed = true)
        val tokenRepository = FakeTokenRepository(storage)

        val token = spyk(mockk<LoginToken>("", relaxed = true))

        test("토큰을 저장할 때 유효성을 검사한다") {
            tokenRepository.save(token)

            verify { token.isValid() }
        }

        test("유효하지 않은 토큰은 TokenStorage에 저장하지 않는다") {
            every { token.isValid() } returns false

            tokenRepository.save(token)

            coVerify(exactly = 0) { storage.save(token) }
        }

        test("유효한 토큰은 TokenStorage에 저장한다") {
            every { token.isValid() } returns true

            tokenRepository.save(token)

            coVerify { storage.save(token) }
        }

        test("유효한 토큰을 저장할 때 유효성을 검사한 후에 TokenStorage에 저장한다") {
            every { token.isValid() } returns true

            tokenRepository.save(token)

            coVerifyOrder {
                token.isValid()
                storage.save(token)
            }
        }
    }

    context("토큰을 로드한다") {
        val storage = mockk<TokenStorage>(relaxed = true)
        val tokenRepository = FakeTokenRepository(storage)

        val token = spyk(mockk<LoginToken>(""))

        test("로드할 토큰이 존재하지 않으면 null을 반환한다") {
            coEvery { storage.load() } returns null

            tokenRepository.load() shouldBe null
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한다") {
            tokenRepository.load()

            coVerify { storage.load() }
        }

        test("토큰을 로드할 때 유효성을 검사한다") {
            coEvery { storage.load() } returns token

            tokenRepository.load()

            verify { token.isValid() }
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한 후에 유효성을 검사한다") {
            coEvery { storage.load() } returns token

            tokenRepository.load()

            coVerifyOrder {
                storage.load()
                token.isValid()
            }
        }

        test("로드한 토큰이 유효하지 않으면 null을 반환한다") {
            every { token.isValid() } returns false

            coEvery { storage.load() } returns token

            tokenRepository.load() shouldBe null
        }

        test("로드한 토큰이 유효하면 반환한다") {
            every { token.isValid() } returns true

            coEvery { storage.load() } returns token

            tokenRepository.load() shouldNotBe null
        }
    }
})