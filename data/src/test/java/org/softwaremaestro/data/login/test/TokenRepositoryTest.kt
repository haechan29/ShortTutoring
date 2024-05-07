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
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.Ok
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound

class TokenRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    mockkObject(FakeTokenStorage)

    val tokenRepository = spyk<FakeTokenRepository>(recordPrivateCalls = true) {
        every { tokenNotFoundFailure } returns mockk<TokenNotFound>()
        every { invalidTokenFailure } returns mockk<InvalidToken>()
    }

    mockkObject(FakeAccessTokenRepository)
    mockkObject(FakeRefreshTokenRepository)

    val token = mockk<LoginToken>(relaxed = true)

    context("토큰을 저장한다") {
        test("토큰을 저장할 때 유효성을 검증한다") {
            tokenRepository.save(token)

            verify { token.isValid() }
        }

        context("토큰이 유효하지 않으면") {
            every { token.isValid() } returns false

            test("TokenStorage에 저장하지 않는다") {
                tokenRepository.save(token)

                coVerify(exactly = 0) { FakeTokenStorage.save(token) }
            }

            test("저장을 실패 처리한다") {
                tokenRepository.save(token) should beInstanceOf<Failure>()
            }

            context("토큰이 유효하지 않아서 저장이 실패했음을 알린다") {
                test("액세스 토큰이 유효하지 않아서 저장이 실패했음을 알린다") {
                    val result = FakeAccessTokenRepository.save(token) as Failure

                    result.message shouldBe INVALID_ACCESS_TOKEN
                }

                test("리프레시 토큰이 유효하지 않아서 저장이 실패했음을 알린다") {
                    val result = FakeRefreshTokenRepository.save(token) as Failure

                    result.message shouldBe INVALID_REFRESH_TOKEN
                }
            }
        }

        context ("토큰이 유효하면") {
            every { token.isValid() } returns true

            test("TokenStorage에 저장한다") {
                tokenRepository.save(token)

                coVerify { FakeTokenStorage.save(token) }
            }

            test("토큰을 저장할 때 유효성을 검증한 후에 TokenStorage에 저장한다") {
                tokenRepository.save(token)

                coVerifyOrder {
                    token.isValid()
                    FakeTokenStorage.save(token)
                }
            }

            test("토큰이 유효하면 TokenStorage에 저장한다") {
                tokenRepository.save(token)

                coVerify { FakeTokenStorage.save(token) }
            }

            test("저장을 성공 처리한다") {
                tokenRepository.save(token) should beInstanceOf<Ok<Any>>()
            }
        }
    }

    context("토큰을 로드한다") {
        test("토큰을 로드할 때 TokenStorage에서 로드한다") {
            tokenRepository.load()

            coVerify { FakeTokenStorage.load() }
        }

        context("로드할 토큰이 존재하지 않으면") {
            coEvery { FakeTokenStorage.load() } returns null

            test("로드를 실패 처리한다") {
                tokenRepository.load() should beInstanceOf<Failure>()
            }

            context("토큰이 존재하지 않아 로드가 실패했음을 알린다") {
                test("액세스 토큰이 존재하지 않아 로드가 실패했음을 알린다") {
                    val result = FakeAccessTokenRepository.load() as Failure

                    result.message shouldBe ACCESS_TOKEN_NOT_FOUND
                }

                test("리프레시 토큰이 존재하지 않아 로드가 실패했음을 알린다") {
                    val result = FakeRefreshTokenRepository.load() as Failure

                    result.message shouldBe REFRESH_TOKEN_NOT_FOUND
                }
            }
        }

        context("로드할 토큰이 존재하면") {
            coEvery { FakeTokenStorage.load() } returns token

            test("로드할 토큰이 존재하면 유효성을 검증한다") {
                tokenRepository.load()

                verify { token.isValid() }
            }

            test("TokenStorage에서 로드한 후에 유효성을 검증한다") {
                tokenRepository.load()

                coVerifyOrder {
                    FakeTokenStorage.load()
                    token.isValid()
                }
            }
        }

        context("로드한 토큰이 유효하지 않으면") {
            every { token.isValid() } returns false

            coEvery { FakeTokenStorage.load() } returns token

            test("로드를 실패 처리한다") {
                tokenRepository.load() should beInstanceOf<Failure>()
            }

            context("토큰이 유효하지 않아 로드가 실패했음을 알린다") {
                test("액세스 토큰이 유효하지 않아 로드가 실패했음을 알린다") {
                    val result = FakeAccessTokenRepository.load() as Failure

                    result.message shouldBe INVALID_ACCESS_TOKEN
                }

                test("리프레시 토큰이 유효하지 않아 로드가 실패했음을 알린다") {
                    val result = FakeRefreshTokenRepository.load() as Failure

                    result.message shouldBe INVALID_REFRESH_TOKEN
                }
            }
        }

        context("로드한 토큰이 유효하면") {
            every { token.isValid() } returns true

            coEvery { FakeTokenStorage.load() } returns token

            test("로드를 성공 처리한다") {
                tokenRepository.load() should beInstanceOf<Ok<Any>>()
            }

            test("성공 응답이 TokenStorage가 반환한 토큰을 포함한다") {
                val result = tokenRepository.load() as Ok

                result.body shouldBe token
            }
        }
    }

    context("유효한 토큰을 불러온다")

    afterEach { unmockkAll() }
})