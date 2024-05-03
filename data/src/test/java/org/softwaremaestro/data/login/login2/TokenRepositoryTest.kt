package org.softwaremaestro.data.login.login2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.softwaremaestro.data.mylogin.fake.FakeTokenRepository
import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.Api
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

class TokenRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("액세스 토큰 인증을 진행한다") {
        val tokenStorage = mockk<TokenStorage>(relaxed = true)
        val validator = mockk<TokenValidator>(relaxed = true)
        val api = mockk<Api>()
        val tokenRepository = spyk(FakeTokenRepository(tokenStorage, validator, api), recordPrivateCalls = true)

        test("액세스 토큰 인증을 시작하면 저장된 액세스 토큰을 로드한다") {
            tokenRepository.authAccessToken()

            coVerify { tokenRepository["readAccessToken"]() }
        }

        test("액세스 토큰을 가지고 있지 않다면 리프레시 토큰 인증을 시작한다") {
            coEvery { tokenRepository["readAccessToken"]() } returns null

            tokenRepository.authAccessToken()

            coVerify { tokenRepository.authRefreshToken() }
        }

        test("액세스 토큰을 가지고 있다면 유효성을 확인한다") {
            val token = mockk<LoginAccessToken>()

            coEvery { tokenRepository["readAccessToken"]() } returns token

            tokenRepository.authAccessToken()

            coVerify { validator.isValid(token) }
        }

        test("유효하지 않은 액세스 토큰을 가지고 있다면 리프레시 토큰 인증을 시작한다") {
            val invalidToken = mockk<LoginAccessToken> {
                every { isValid() } returns false
            }

            coEvery { tokenRepository["readAccessToken"]() } returns invalidToken

            tokenRepository.authAccessToken()

            coVerify { tokenRepository.authRefreshToken() }
        }

        test("유효한 액세스 토큰을 가지고 있다면 토큰을 서버로 전송한다") {
            val validToken = mockk<LoginAccessToken> {
                every { isValid() } returns true
            }

            coEvery { tokenRepository["readAccessToken"]() } returns validToken

            tokenRepository.authAccessToken()

            coVerify { api.send(ofType<LoginRequestDto>()) }
        }
    }

    context("토큰을 저장한다") {
        val storage = mockk<TokenStorage>(relaxed = true)
        val validator = mockk<TokenValidator>(relaxed = true)
        val api = mockk<Api>(relaxed = true)
        val tokenRepository = FakeTokenRepository(storage, validator, api)

        val token = mockk<LoginToken>()

        test("토큰을 저장할 때 유효성을 검사한다") {
            tokenRepository.save(token)

            verify { validator.isValid(token) }
        }

        test("유효하지 않은 토큰은 TokenStorage에 저장하지 않는다") {
            every { validator.isValid(token) } returns false

            tokenRepository.save(token)

            coVerify(exactly = 0) { storage.save(token) }
        }

        test("유효한 토큰은 TokenStorage에 저장한다") {
            every { validator.isValid(token) } returns true

            tokenRepository.save(token)

            coVerify { storage.save(token) }
        }

        test("유효한 토큰을 저장할 때 유효성을 검사한 후에 TokenStorage에 저장한다") {
            every { validator.isValid(token) } returns true

            tokenRepository.save(token)

            coVerifyOrder {
                validator.isValid(token)
                storage.save(token)
            }
        }
    }

    context("토큰을 로드한다") {
        val storage = mockk<TokenStorage>(relaxed = true)
        val validator = mockk<TokenValidator>(relaxed = true)
        val api = mockk<Api>(relaxed = true)
        val tokenRepository = FakeTokenRepository(storage, validator, api)

        test("로드할 토큰이 존재하지 않으면 null을 반환한다") {
            coEvery { storage.load() } returns null

            tokenRepository.load() shouldBe null
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한다") {
            tokenRepository.load()

            coVerify { storage.load() }
        }

        test("토큰을 로드할 때 유효성을 검사한다") {
            tokenRepository.load()

            verify { validator.isValid(any()) }
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한 후에 유효성을 검사한다") {
            tokenRepository.load()

            coVerifyOrder {
                storage.load()
                validator.isValid(any())
            }
        }

        test("로드한 토큰이 유효하지 않으면 null을 반환한다") {
            every { validator.isValid(any()) } returns false

            tokenRepository.load() shouldBe null
        }

        test("로드한 토큰이 유효하면 반환한다") {
            every { validator.isValid(any()) } returns true

            tokenRepository.load() shouldNotBe null
        }
    }
})