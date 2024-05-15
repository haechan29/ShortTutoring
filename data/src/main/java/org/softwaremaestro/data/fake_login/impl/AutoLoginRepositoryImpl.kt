package org.softwaremaestro.data.fake_login.impl

import org.softwaremaestro.domain.fake_login.AutoLoginRepository
import org.softwaremaestro.data.fake_login.dto.EmptyRequestDto
import org.softwaremaestro.data.fake_login.legacy.AutoLoginApi
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.domain.fake_login.util.dtoOrNull
import org.softwaremaestro.domain.fake_login.entity.Role
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import javax.inject.Inject

class AutoLoginRepositoryImpl @Inject constructor(
    private val autoLoginApi: AutoLoginApi
): AutoLoginRepository {
    override suspend fun autologin(): NetworkResult<Role?> {
        val response = autoLoginApi.sendRequest(EmptyRequestDto)
        val dto = response.dtoOrNull() ?: return response as NetworkFailure
        return NetworkSuccess(dto.role)
    }
}