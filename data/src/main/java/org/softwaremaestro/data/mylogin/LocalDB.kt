package org.softwaremaestro.data.mylogin

import org.softwaremaestro.domain.mylogin.entity.AccessLoginToken

interface LocalDB {
    suspend fun readAccessToken(): AccessLoginToken?
}