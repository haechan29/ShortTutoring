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
import io.mockk.verifyOrder
import kotlinx.serialization.descriptors.StructureKind
import net.bytebuddy.matcher.ElementMatchers.any
import org.softwaremaestro.data.login.fake.FakeLocalDB
import org.softwaremaestro.data.login.fake.FakeServer
import org.softwaremaestro.data.login.fake.FakeMyLoginRepositoryImpl
import org.softwaremaestro.data.login.fake.FakeTokenManager
import org.softwaremaestro.data.login.fake.FakeTokenStorage
import org.softwaremaestro.data.login.fake.FakeTokenValidator
import org.softwaremaestro.data.mylogin.TokenManager
import org.softwaremaestro.data.mylogin.TokenStorage
import org.softwaremaestro.data.mylogin.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Server

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val storage = mockk<TokenStorage>(relaxed = true)
    val validator = mockk<TokenValidator>(relaxed = true)
    val server = mockk<Server>(relaxed = true)
    val tokenManager = mockk<TokenManager>(relaxed = true)
    val repository = FakeMyLoginRepositoryImpl(storage, validator, server, tokenManager)

    val token = mockk<LoginToken>()

    context("토큰을 저장한다") {
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
})