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
import io.mockk.verify
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.data.fake_login.fake.AccessTokenStorageRepositoryImpl
import org.softwaremaestro.data.fake_login.fake.RefreshTokenStorageRepositoryImpl
import org.softwaremaestro.data.fake_login.fake.LoginTokenStorageRepositoryImpl
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.ACCESS_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.INVALID_ACCESS_TOKEN
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.INVALID_REFRESH_TOKEN
import org.softwaremaestro.domain.fake_login.result.Failure.Companion.REFRESH_TOKEN_NOT_FOUND
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.InvalidLoginToken
import org.softwaremaestro.data.fake_login.dto.EmptyResponseDto
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.domain.fake_login.result.LoginTokenNotFound
import org.softwaremaestro.data.fake_login.legacy.LoginTokenStorage
import org.softwaremaestro.data.fake_login.legacy.UserIdentifier
import org.softwaremaestro.domain.fake_login.entity.Validatable
import org.softwaremaestro.data.fake_login.dto.LocalTokenResponseDto
import org.softwaremaestro.data.fake_login.legacy.AccessTokenStorage
import org.softwaremaestro.data.fake_login.legacy.RefreshTokenStorage

class TokenStorageRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val accessTokenStorage = mockk<AccessTokenStorage>(relaxed = true)
    val refreshTokenStorage = mockk<RefreshTokenStorage>(relaxed = true)
    val loginTokenStorage = mockk<LoginTokenStorage>(relaxed = true)

    val userIdentifier = mockk<UserIdentifier>(relaxed = true)

    val loginTokenNotFound = mockk<LoginTokenNotFound>(relaxed = true)
    val invalidLoginToken = mockk<InvalidLoginToken>(relaxed = true)

    val accessTokenRepository = spyk(
        AccessTokenStorageRepositoryImpl(accessTokenStorage, userIdentifier),
        recordPrivateCalls = true
    )

    val refreshTokenRepository = spyk(
        RefreshTokenStorageRepositoryImpl(refreshTokenStorage, userIdentifier),
        recordPrivateCalls = true
    )

    val tokenRepository = spyk(
        object: LoginTokenStorageRepositoryImpl(
            loginTokenStorage,
            userIdentifier,
            loginTokenNotFound,
            invalidLoginToken
        ) {} , recordPrivateCalls = true
    ) {
        every { this@spyk["isValid"](ofType<Validatable>()) } returns true
        coEvery { this@spyk["saveToStorage"](ofType<LoginToken>()) } returns mockk<Unit>(relaxed = true)
        coEvery { this@spyk["loadFromStorage"]() } returns mockk<LoginToken>(relaxed = true)
        coEvery { this@spyk["isUserIdentified"]() } returns true
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