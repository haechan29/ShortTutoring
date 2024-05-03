package org.softwaremaestro.data.mylogin.dto

import org.softwaremaestro.domain.mylogin.entity.LoginRequestDto

data class LoginRequestDto(private val id: String, private val password: String): LoginRequestDto {
    override fun isValid(): Boolean {
        return !(id.isEmpty() || password.isEmpty())
    }
}