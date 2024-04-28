package org.softwaremaestro.data.login

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.softwaremaestro.data.common.utils.SavedToken
import org.softwaremaestro.data.infra.SharedPrefs
import org.softwaremaestro.data.login.model.LoginReqDto
import org.softwaremaestro.data.login.model.RegisterFCMTokenReqDto
import org.softwaremaestro.data.login.remote.FCMApi
import org.softwaremaestro.data.login.remote.LoginApi
import org.softwaremaestro.domain.common.BaseResult
import org.softwaremaestro.domain.login.LoginRepository
import org.softwaremaestro.domain.login.MyLoginRepository
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.domain.login.entity.UserVO
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyLoginRepositoryImpl @Inject constructor(): MyLoginRepository {
    private var savedToken: LoginToken? = null

    override suspend fun save(token: LoginToken) {
        withContext(Dispatchers.IO) {
            delay(1000)
            // TODO
            savedToken = token
        }
    }

    override suspend fun load(): LoginToken {
        return withContext(Dispatchers.IO) {
            delay(1000)
            // TODO
            savedToken!!
        }
    }
}