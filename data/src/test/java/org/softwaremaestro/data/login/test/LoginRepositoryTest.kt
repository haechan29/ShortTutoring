package org.softwaremaestro.data.login.test

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
import org.softwaremaestro.data.login.fake.FakeMyLoginRepositoryImpl
import org.softwaremaestro.domain.mylogin.entity.TokenManager
import org.softwaremaestro.domain.mylogin.entity.TokenStorage
import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.LoginResult
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Api

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("토큰을 저장한다") {
        val storage = mockk<TokenStorage>(relaxed = true)
        val validator = mockk<TokenValidator>(relaxed = true)
        val api = mockk<Api>(relaxed = true)
        val tokenManager = mockk<TokenManager>(relaxed = true)
        val repository = FakeMyLoginRepositoryImpl(storage, validator, api, tokenManager)

        val token = mockk<LoginToken>()

        test("토큰을 저장할 때 유효성을 검사한다") {
            repository.save(token)

            verify { validator.isValid(token) }
        }

        test("유효하지 않은 토큰은 TokenStorage에 저장하지 않는다") {
            every { validator.isValid(token) } returns false

            repository.save(token)

            coVerify(exactly = 0) { storage.save(token) }
        }

        test("유효한 토큰은 TokenStorage에 저장한다") {
            every { validator.isValid(token) } returns true

            repository.save(token)

            coVerify { storage.save(token) }
        }

        test("유효한 토큰을 저장할 때 유효성을 검사한 후에 TokenStorage에 저장한다") {
            every { validator.isValid(token) } returns true

            repository.save(token)

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
        val tokenManager = mockk<TokenManager>(relaxed = true)
        val repository = FakeMyLoginRepositoryImpl(storage, validator, api, tokenManager)

        test("로드할 토큰이 존재하지 않으면 null을 반환한다") {
            coEvery { storage.load() } returns null

            repository.load() shouldBe null
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한다") {
            repository.load()

            coVerify { storage.load() }
        }

        test("토큰을 로드할 때 유효성을 검사한다") {
            repository.load()

            verify { validator.isValid(any()) }
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한 후에 유효성을 검사한다") {
            repository.load()

            coVerifyOrder {
                storage.load()
                validator.isValid(any())
            }
        }

        test("로드한 토큰이 유효하지 않으면 null을 반환한다") {
            every { validator.isValid(any()) } returns false

            repository.load() shouldBe null
        }

        test("로드한 토큰이 유효하면 반환한다") {
            every { validator.isValid(any()) } returns true

            repository.load() shouldNotBe null
        }
    }

    context("로그인한다") {
        val storage = mockk<TokenStorage>(relaxed = true)
        val tokenValidator = mockk<TokenValidator>(relaxed = true)
        val tokenManager = mockk<TokenManager>(relaxed = true)

        val validId = "id"
        val validPassword = "password"
        val invalidId = ""
        val invalidPassword = ""

        context("아이디와 비밀번호를 검증한다") {
            val api = mockk<Api>(relaxed = true)
            val repository = FakeMyLoginRepositoryImpl(storage, tokenValidator, api, tokenManager)

            test("유효하지 않은 아이디를 입력하면 로그인 정보가 유효하지 않다는 결과를 반환한다") {
                val result = repository.login(id = invalidId, password = validPassword)
                result shouldBe LoginResult.INVALID_LOGIN_INFO
            }

            test("유효하지 않은 비밀번호를 입력하면 로그인 정보가 유효하지 않다는 결과를 반환한다") {
                val result = repository.login(id = validId, password = invalidPassword)
                result shouldBe LoginResult.INVALID_LOGIN_INFO
            }
        }

        test("유효한 아이디와 비밀번호를 입력하면 API를 호출한다") {
            val api = spyk<Api>()
            val repository = FakeMyLoginRepositoryImpl(storage, tokenValidator, api, tokenManager)

            repository.login(validId, validPassword)

            coVerify { api.send(any()) }
        }

        test("유효한 아이디와 비밀번호를 입력하면 로그인이 성공했다는 결과를 반환한다") {
            val api = mockk<Api>(relaxed = true)
            val repository = FakeMyLoginRepositoryImpl(storage, tokenValidator, api, tokenManager)

            repository.login(validId, validPassword) shouldBe LoginResult.OK
        }
    }
})