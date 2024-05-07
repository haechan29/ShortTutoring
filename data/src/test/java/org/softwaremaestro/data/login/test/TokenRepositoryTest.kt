package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeTokenStorage
import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.ACCESS_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_ACCESS_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_REFRESH_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.REFRESH_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenStorage

class TokenRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val fakeAccessTokenRepository = FakeAccessTokenRepository
    val fakeRefreshTokenRepository = FakeRefreshTokenRepository

    mockkObject(fakeAccessTokenRepository)
    mockkObject(fakeRefreshTokenRepository)

    val tokenRepository = spyk<FakeTokenRepository<LoginToken>>(recordPrivateCalls = true) {
        every { tokenNotFoundFailure } returns mockk<TokenNotFound>()
        every { invalidTokenFailure } returns mockk<InvalidToken>()
    }

    context("토큰을 저장한다") {
        every { tokenRepository["saveToStorage"](ofType<LoginToken>()) } returns Unit

        val token = mockk<LoginToken>(relaxed = true)

        test("토큰을 저장할 때 유효성을 검증한다") {
            tokenRepository.save(token)

            verify { token.isValid() }
        }

        context("토큰이 유효하지 않으면") {
            val invalidToken = mockk<LoginToken> {
                every { isValid() } returns false
            }

            val result = tokenRepository.save(invalidToken)

            test("TokenStorage에 저장하지 않는다") {
                coVerify(exactly = 0) { tokenRepository["saveToStorage"](invalidToken) }
            }

            test("저장을 실패 처리한다") {
                result should beInstanceOf<Failure>()
            }

            context("토큰이 유효하지 않아서 저장이 실패했음을 알린다") {
                val invalidAccessToken = mockk<LoginAccessToken> {
                    every { isValid() } returns false
                }

                val invalidRefreshToken = mockk<LoginRefreshToken> {
                    every { isValid() } returns false
                }

                test("액세스 토큰이 유효하지 않아서 저장이 실패했음을 알린다") {
                    val result = fakeAccessTokenRepository.save(invalidAccessToken) as Failure

                    result.message shouldBe INVALID_ACCESS_TOKEN
                }

                test("리프레시 토큰이 유효하지 않아서 저장이 실패했음을 알린다") {
                    val result = fakeRefreshTokenRepository.save(invalidRefreshToken) as Failure

                    result.message shouldBe INVALID_REFRESH_TOKEN
                }
            }
        }

        context("토큰이 유효하면") {
            val validToken = mockk<LoginToken> {
                every { isValid() } returns true
            }

            val result = tokenRepository.save(validToken)

            test("TokenStorage에 저장한다") {
                coVerify { tokenRepository["saveToStorage"](validToken) }
            }

            test("토큰을 저장할 때 유효성을 검증한 후에 TokenStorage에 저장한다") {
                coVerifyOrder {
                    validToken.isValid()
                    tokenRepository["saveToStorage"](validToken)
                }
            }

            test("저장을 성공 처리한다") {
                result should beInstanceOf<Ok<Any>>()
            }
        }
    }

    context("토큰을 로드한다") {
        val token = mockk<LoginToken>(relaxed = true)
        every { tokenRepository["loadFromStorage"]() } returns token

        test("토큰을 로드할 때 TokenStorage에서 로드한다") {
            tokenRepository.load()

            coVerify { tokenRepository["loadFromStorage"]() }
        }

        context("로드할 토큰이 존재하지 않으면") {
            coEvery { tokenRepository["loadFromStorage"]() } returns null
            coEvery { fakeAccessTokenRepository["loadFromStorage"]() } returns null
            coEvery { fakeRefreshTokenRepository["loadFromStorage"]() } returns null

            test("로드를 실패 처리한다") {
                tokenRepository.load() should beInstanceOf<Failure>()
            }

            context("토큰이 존재하지 않아 로드가 실패했음을 알린다") {
                test("액세스 토큰이 존재하지 않아 로드가 실패했음을 알린다") {
                    val result = fakeAccessTokenRepository.load() as Failure

                    result.message shouldBe ACCESS_TOKEN_NOT_FOUND
                }

                test("리프레시 토큰이 존재하지 않아 로드가 실패했음을 알린다") {
                    val result = fakeRefreshTokenRepository.load() as Failure

                    result.message shouldBe REFRESH_TOKEN_NOT_FOUND
                }
            }
        }

        context("로드할 토큰이 존재하면") {
            coEvery { tokenRepository["loadFromStorage"]() } returns token

            test("로드할 토큰이 존재하면 유효성을 검증한다") {
                tokenRepository.load()

                verify { token.isValid() }
            }

            test("TokenStorage에서 로드한 후에 유효성을 검증한다") {
                tokenRepository.load()

                coVerifyOrder {
                    tokenRepository["loadFromStorage"]()
                    token.isValid()
                }
            }
        }

        context("로드한 토큰이 유효하지 않으면") {
            val invalidToken = mockk<LoginToken> {
                every { isValid() } returns false
            }

            coEvery { tokenRepository["loadFromStorage"]() } returns invalidToken

            coEvery { fakeAccessTokenRepository["loadFromStorage"]() } returns invalidToken

            coEvery { fakeRefreshTokenRepository["loadFromStorage"]() } returns invalidToken

            test("로드를 실패 처리한다") {
                tokenRepository.load() should beInstanceOf<Failure>()
            }

            context("토큰이 유효하지 않아 로드가 실패했음을 알린다") {
                test("액세스 토큰이 유효하지 않아 로드가 실패했음을 알린다") {
                    val result = fakeAccessTokenRepository.load() as Failure

                    result.message shouldBe INVALID_ACCESS_TOKEN
                }

                test("리프레시 토큰이 유효하지 않아 로드가 실패했음을 알린다") {
                    val result = fakeRefreshTokenRepository.load() as Failure

                    result.message shouldBe INVALID_REFRESH_TOKEN
                }
            }
        }

        context("로드한 토큰이 유효하면") {
            val validToken = mockk<LoginToken> {
                every { isValid() } returns true
            }

            coEvery { tokenRepository["loadFromStorage"]() } returns validToken

            test("로드를 성공 처리한다") {
                tokenRepository.load() should beInstanceOf<Ok<Any>>()
            }

            test("성공 응답이 TokenStorage가 반환한 토큰을 포함한다") {
                val result = tokenRepository.load() as Ok

                result.body shouldBe validToken
            }
        }
    }

    context("유효한 토큰을 불러온다")

    afterEach { unmockkAll() }
})