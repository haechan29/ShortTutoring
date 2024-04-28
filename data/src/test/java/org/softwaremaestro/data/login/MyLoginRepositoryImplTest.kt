package org.softwaremaestro.data.login

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.softwaremaestro.data.login.MyLoginRepositoryImpl
import org.softwaremaestro.domain.login.MyLoginRepository
import org.softwaremaestro.domain.login.entity.InvalidTokenException
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.domain.login.entity.TokenNotFoundException

class MyLoginRepositoryImplTest: FunSpec({
    lateinit var repository: MyLoginRepository

    beforeEach {
        repository = MyLoginRepositoryImpl()
    }

    context("토큰을 저장하고 로드한다") {
        test("저장되지 않은 토큰을 로드하면 TokenNotFoundException이 발생한다") {
            shouldThrow<TokenNotFoundException> {
                repository.load()
            }
        }

        test("저장된 토큰을 로드하면 컨텐츠가 유지된다") {
            val token = LoginToken("content", true)
            repository.save(token)
            repository.load().content shouldBe "content"
        }
    }


})