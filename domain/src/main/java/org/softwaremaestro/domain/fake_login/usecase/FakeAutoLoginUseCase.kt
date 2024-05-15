package org.softwaremaestro.domain.fake_login.usecase

import org.softwaremaestro.domain.fake_login.AutoLoginRepository
import org.softwaremaestro.domain.fake_login.entity.Role
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import javax.inject.Inject

class FakeAutoLoginUseCase @Inject constructor(private val autoLoginRepository: AutoLoginRepository) {
    suspend operator fun invoke(): NetworkResult<Role?> {
        return autoLoginRepository.autologin()
    }
}