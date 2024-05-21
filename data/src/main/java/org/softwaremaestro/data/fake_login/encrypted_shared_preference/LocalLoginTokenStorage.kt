package org.softwaremaestro.data.fake_login.encrypted_shared_preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.softwaremaestro.data.fake_login.fake.FakeLoginAccessToken
import org.softwaremaestro.data.fake_login.legacy.LoginTokenStorage
import org.softwaremaestro.domain.fake_login.entity.LoginToken

open class LocalLoginTokenStorage(
    private val context: Context,
    private val fileName: String,
    private val tokenAlias: String
): LoginTokenStorage {
    private val encryptedSharedPreferences = getEncryptedSharedPreferences()

    override suspend fun save(loginToken: LoginToken) {
        val editor = encryptedSharedPreferences?.edit() ?: return
        editor.putString(tokenAlias, loginToken.content)
        editor.apply()
    }

    override suspend fun load(): LoginToken? {
        val content = encryptedSharedPreferences?.getString(tokenAlias, null) ?: return null
        return FakeLoginAccessToken(content)
    }

    override suspend fun clear() {
        val editor: SharedPreferences.Editor = encryptedSharedPreferences?.edit() ?: return
        editor.remove(ACCESS_TOKEN_ALIAS)
        editor.remove(REFRESH_TOKEN_ALIAS)
        editor.clear()
        editor.apply()
    }

    private fun getEncryptedSharedPreferences(): SharedPreferences? {
        return try {
            EncryptedSharedPreferences.create(
                fileName,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val ACCESS_TOKEN_ALIAS = "access token alias"
        const val REFRESH_TOKEN_ALIAS = "refresh token alias"

        const val ACCESS_TOKEN_FILE_NAME = "access token file"
        const val REFRESH_TOKEN_FILE_NAME = "refresh token file"
    }
}

class LocalAccessTokenStorage(context: Context): LocalLoginTokenStorage(context, ACCESS_TOKEN_FILE_NAME, ACCESS_TOKEN_ALIAS)
class LocalRefreshTokenStorage(context: Context): LocalLoginTokenStorage(context, REFRESH_TOKEN_FILE_NAME, REFRESH_TOKEN_ALIAS)