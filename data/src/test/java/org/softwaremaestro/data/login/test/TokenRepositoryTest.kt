package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.softwaremaestro.data.mylogin.fake.FakeTokenRepository
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.ACCESS_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_ACCESS_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_REFRESH_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.REFRESH_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.TokenAuthenticator
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

class TokenRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val storage = mockk<TokenStorage>(relaxed = true)
    val accessTokenAuthenticator = mockk<TokenAuthenticator>(relaxed = true)
    val refreshTokenAuthenticator = mockk<TokenAuthenticator>(relaxed = true)
    val tokenRepository = spyk(FakeTokenRepository(storage, accessTokenAuthenticator, refreshTokenAuthenticator), recordPrivateCalls = true)

    val token = mockk<LoginToken>("", relaxed = true)

    context("토큰을 저장한다") {
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