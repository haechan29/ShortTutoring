package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.softwaremaestro.data.mylogin.fake.FakeAccessTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeRefreshTokenRepository
import org.softwaremaestro.data.mylogin.fake.FakeTokenRepository
import org.softwaremaestro.domain.mylogin.entity.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.ACCESS_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_ACCESS_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.INVALID_REFRESH_TOKEN
import org.softwaremaestro.domain.mylogin.entity.Failure.Companion.REFRESH_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.InvalidToken
import org.softwaremaestro.domain.mylogin.entity.LocalTokenResponseDto
import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.LoginToken
import org.softwaremaestro.domain.mylogin.entity.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.TokenNotFound
import org.softwaremaestro.domain.mylogin.entity.TokenStorage
import org.softwaremaestro.domain.mylogin.entity.UserIdentifier
import org.softwaremaestro.domain.mylogin.entity.Validatable

class TokenRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val accessTokenStorage = mockk<TokenStorage<LoginAccessToken>>(relaxed = true)
    val refreshTokenStorage = mockk<TokenStorage<LoginRefreshToken>>(relaxed = true)
    val tokenStorage = mockk<TokenStorage<LoginToken>>(relaxed = true)

    val userIdentifier = mockk<UserIdentifier>(relaxed = true)

    val tokenNotFoundFailure = mockk<TokenNotFound<LoginToken>>(relaxed = true)
    val invalidTokenFailure = mockk<InvalidToken<LoginToken>>(relaxed = true)

    val accessTokenRepository = spyk(
        object: FakeAccessTokenRepository(accessTokenStorage, userIdentifier) {}, recordPrivateCalls = true
    )

    val refreshTokenRepository = spyk(
        object: FakeRefreshTokenRepository(refreshTokenStorage, userIdentifier) {}, recordPrivateCalls = true
    )

    val tokenRepository = spyk(
        object: FakeTokenRepository<LoginToken>(tokenStorage, userIdentifier, tokenNotFoundFailure, invalidTokenFailure) {} , recordPrivateCalls = true
    ) {
        every { this@spyk["isValid"](ofType<Validatable>()) } returns true
        coEvery { this@spyk["saveToStorage"](ofType<LoginToken>()) } returns mockk<Unit>(relaxed = true)
        coEvery { this@spyk["loadFromStorage"]() } returns mockk<LoginToken>(relaxed = true)
        coEvery { this@spyk["isUserIdentified"]() } returns true
        every { this@spyk["toDto"](ofType<LoginToken>()) } returns mockk<LocalTokenResponseDto>(relaxed = true)
    }

    // 저장
    context("토큰을 저장할 때") {
        tokenRepository.save(mockk<LoginToken>(relaxed = true))

        test("유효성을 검증한다") {
            verify { tokenRepository["isValid"](ofType<Validatable>()) }
        }
    }

    context("토큰이 유효하지 않으면") {
        every { tokenRepository["isValid"](ofType<Validatable>()) } returns false

        val result = tokenRepository.save(mockk<LoginToken>(relaxed = true))

        test("TokenStorage에 저장하지 않는다") {
            coVerify(exactly = 0) { tokenRepository["saveToStorage"](ofType<LoginToken>()) }
        }

        test("저장을 실패 처리한다") {
            result should beInstanceOf<NetworkFailure>()
        }
    }

    context("액세스 토큰이 유효하지 않으면") {
        every { accessTokenRepository["isValid"](ofType<Validatable>()) } returns false

        val result = accessTokenRepository.save(mockk<LoginAccessToken>(relaxed = true))

        test("액세스 토큰이 유효하지 않아서 저장이 실패했음을 알린다") {
            (result as NetworkFailure).message shouldBe INVALID_ACCESS_TOKEN
        }
    }

    context("리프레시 토큰이 유효하지 않으면") {
        every { refreshTokenRepository["isValid"](ofType<Validatable>()) } returns false

        val result = refreshTokenRepository.save(mockk<LoginRefreshToken>(relaxed = true))

        test("리프레시 토큰이 유효하지 않아서 저장이 실패했음을 알린다") {
            (result as NetworkFailure).message shouldBe INVALID_REFRESH_TOKEN
        }
    }

    context("토큰이 유효하면") {
        val result = tokenRepository.save(mockk<LoginRefreshToken>(relaxed = true))

        test("TokenStorage에 저장한다") {
            coVerify { tokenRepository["saveToStorage"](ofType<LoginToken>()) }
        }

        test("저장을 성공 처리한다") {
            result should beInstanceOf<NetworkSuccess<EmptyResponseDto>>()
        }
    }

    // 로드
    context("로드할 토큰이 존재하지 않으면") {
        coEvery { tokenRepository["loadFromStorage"]() } returns null

        test("로드를 실패 처리한다") {
            tokenRepository.load() should beInstanceOf<NetworkFailure>()
        }
    }

    context("로드할 액세스 토큰이 존재하지 않으면") {
        coEvery { accessTokenRepository["loadFromStorage"]() } returns null

        val result = accessTokenRepository.load()

        test("액세스 토큰이 존재하지 않아 로드가 실패했음을 알린다") {
            (result as NetworkFailure).message shouldBe ACCESS_TOKEN_NOT_FOUND
        }
    }

    context("로드할 리프레시 토큰이 존재하지 않으면") {
        coEvery { refreshTokenRepository["loadFromStorage"]() } returns null

        val result = refreshTokenRepository.load()

        test("리프레시 토큰이 존재하지 않아 로드가 실패했음을 알린다") {
            (result as NetworkFailure).message shouldBe REFRESH_TOKEN_NOT_FOUND
        }
    }

    context("로드할 토큰이 존재하면") {
        tokenRepository.load()

        test("사용자를 식별한다") {
            coVerify { tokenRepository["isUserIdentified"]() }
        }
    }

    context ("사용자를 식별하지 못하면") {
        coEvery { tokenRepository["isUserIdentified"]() } returns false

        val result = tokenRepository.load()

        test("로드를 실패 처리한다") {
            result should beInstanceOf<NetworkFailure>()
        }
    }

    context ("사용자를 식별하면") {
        val result = tokenRepository.load()

        test("로드를 성공 처리한다") {
            result should beInstanceOf<NetworkSuccess<LocalTokenResponseDto>>()
        }
    }
})