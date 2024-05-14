package org.softwaremaestro.domain.mylogin.entity.dto

import org.softwaremaestro.domain.mylogin.entity.LoginAccessToken
import org.softwaremaestro.domain.mylogin.entity.LoginRefreshToken
import org.softwaremaestro.domain.mylogin.entity.Role

interface ResponseDto {
    fun containsNullField(): Boolean
}

interface LoginResponseDto: ResponseDto {
    val role: Role?
    val accessToken: LoginAccessToken?
    val refreshToken: LoginRefreshToken?
}

interface LocalTokenResponseDto: ResponseDto {
    val accessToken: LoginAccessToken
}

object EmptyResponseDto: ResponseDto {
    override fun containsNullField() = false
}