package org.softwaremaestro.domain.fake_login

import org.softwaremaestro.domain.fake_login.entity.Role
import org.softwaremaestro.domain.fake_login.result.NetworkResult

interface AutoLoginRepository {
    suspend fun autologin(): NetworkResult<Role?>
}