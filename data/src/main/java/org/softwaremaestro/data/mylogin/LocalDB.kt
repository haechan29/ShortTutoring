package org.softwaremaestro.data.mylogin

interface LocalDB {
    suspend fun readAccessToken(): AccessToken?
}