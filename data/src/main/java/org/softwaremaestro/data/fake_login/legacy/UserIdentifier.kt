package org.softwaremaestro.data.fake_login.legacy

interface UserIdentifier {
    suspend fun identifyUser(): Boolean
}