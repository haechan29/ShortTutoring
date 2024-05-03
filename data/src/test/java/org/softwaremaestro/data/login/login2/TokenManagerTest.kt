package org.softwaremaestro.data.login.login2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.softwaremaestro.data.login.fake.FakeTokenManager
import org.softwaremaestro.domain.mylogin.entity.LocalDB
import org.softwaremaestro.domain.mylogin.entity.TokenValidator
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRequestDto
import org.softwaremaestro.domain.mylogin.entity.Api

class TokenManagerTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val localDB = mockk<LocalDB>(relaxed = true)
    val validator = mockk<TokenValidator>(relaxed = true)
    val api = mockk<Api>()
    val tokenManager = spyk(FakeTokenManager(localDB, validator, api))

    context("액세스 토큰 인증을 진행한다") {
        test("액세스 토큰 인증을 시작하면 저장된 액세스 토큰을 로드한다") {
            tokenManager.authAccessToken()

            coVerify { localDB.readAccessToken() }
        }

        test("액세스 토큰을 가지고 있지 않다면 리프레시 토큰 인증을 시작한다") {
            coEvery { localDB.readAccessToken() } returns null

            tokenManager.authAccessToken()

            coVerify { tokenManager.authRefreshToken() }
        }

        test("액세스 토큰을 가지고 있다면 유효성을 확인한다") {
            val token = mockk<LoginAccessToken>()

            coEvery { localDB.readAccessToken() } returns token

            tokenManager.authAccessToken()

            coVerify { validator.isValid(token) }
        }

        test("유효하지 않은 액세스 토큰을 가지고 있다면 리프레시 토큰 인증을 시작한다") {
            val invalidToken = mockk<LoginAccessToken> {
                every { isValid() } returns true
            }

            coEvery { localDB.readAccessToken() } returns invalidToken

            tokenManager.authAccessToken()

            coVerify { tokenManager.authRefreshToken() }
        }

        test("유효한 액세스 토큰을 가지고 있다면 토큰을 서버로 전송한다") {
            val validToken = mockk<LoginAccessToken> {
                every { isValid() } returns true
            }

            coEvery { localDB.readAccessToken() } returns validToken

            tokenManager.authAccessToken()

            coVerify { api.send(ofType<LoginRequestDto>()) }
        }
    }
})