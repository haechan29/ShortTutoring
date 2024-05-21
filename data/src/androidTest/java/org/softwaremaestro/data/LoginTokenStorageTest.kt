package org.softwaremaestro.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.softwaremaestro.data.fake_login.encrypted_shared_preference.LocalLoginTokenStorage
import org.softwaremaestro.domain.fake_login.entity.LoginToken


class LoginTokenStorageTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @DisplayName("로드할 토큰이 없으면 null을 반환한다")
    @Test
    fun returnsNullWhenNoTokenWasSaved() = runTest {
        assertNull(tokenStorage.load())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("토큰을 저장하고 로드했을 때 내용을 유지한다")
    fun returnsSavedTokenWhenSaveAndLoadAToken() = runTest {
        // Given
        val content = "content"

        val token = Mockito.mock<LoginToken>()

        `when`(token.content).thenReturn(content)

        // When
        tokenStorage.save(token)

        val loadedToken = tokenStorage.load()

        // Then
        assertEquals(loadedToken!!.content, token.content)
    }

    @After
    fun tearDown() = runBlocking {
        tokenStorage.clear()
    }

    companion object {
        lateinit var tokenStorage: LocalLoginTokenStorage

        @JvmStatic
        @BeforeClass
        fun setUp() {
            val context: Context = ApplicationProvider.getApplicationContext()
            tokenStorage = Mockito.spy(LocalLoginTokenStorage(context, "", ""))
        }
    }
}