package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken

interface LocalDB {
    suspend fun readAccessToken(): LoginAccessToken?
}