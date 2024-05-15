package org.softwaremaestro.data.fake_login.fake

import org.softwaremaestro.data.fake_login.legacy.UserIdentifier
import javax.inject.Inject

class FakeUserIdentifier @Inject constructor(): UserIdentifier {
    override suspend fun identifyUser(): Boolean {
        return true
    }
}