package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import org.softwaremaestro.data.login.fake.FakeMyLoginRepositoryImpl
import org.softwaremaestro.data.login.fake.FakeTokenStorage
import org.softwaremaestro.data.login.fake.FakeTokenValidator
import org.softwaremaestro.domain.login.entity.LoginToken

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val storage = spyk(FakeTokenStorage)
    val validator = spyk(FakeTokenValidator)
    val repository = FakeMyLoginRepositoryImpl(storage, validator)

    val token = LoginToken("") { true }

    context("토큰을 저장한다") {
        beforeEach {
            repository.save(token)
        }

        test("토큰을 저장할 때 유효성을 검사한다") {
            verify(exactly = 1) { validator["validate"](token) }
        }

        test("토큰을 저장할 때 TokenStorage에 저장한다") {
            verify(exactly = 1) { storage["save"](token) }
        }

        test("토큰을 저장할 때 유효성을 검사한 후에 TokenStorage에 저장한다") {
            verifyOrder {
                validator["validate"](token)
                storage["save"](token)
            }
        }
    }

    context("토큰을 로드한다") {
        beforeEach {
            FakeTokenStorage.save(token)
            repository.load()
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한다") {
            verify(exactly = 1) { storage["load"]() }
        }

        test("토큰을 로드할 때 유효성을 검사한다") {
            verify(exactly = 1) { validator["validate"](token) }
        }

        test("토큰을 로드할 때 TokenStorage에서 로드한 후에 유효성을 검사한다") {
            verifyOrder {
                storage["load"]()
                validator["validate"](token)
            }
        }
    }
})