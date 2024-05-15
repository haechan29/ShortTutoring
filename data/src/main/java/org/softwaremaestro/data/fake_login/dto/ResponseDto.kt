package org.softwaremaestro.data.fake_login.dto

import org.softwaremaestro.domain.fake_login.util.containsNullField
import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginInfo
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.NullFieldCheckable
import org.softwaremaestro.domain.fake_login.entity.Role

interface ResponseDto: NullFieldCheckable {
    override fun containsNullField(): Boolean {
        return containsNullField(this)
    }
}

data class IssueTokenResponseDto(
    val accessToken: LoginAccessToken?,
    val refreshToken: LoginRefreshToken?
): ResponseDto {
    fun toLoginInfo() = LoginInfo(accessToken, refreshToken)
}

data class AutoLoginResponseDto(val role: Role?): ResponseDto

interface LocalTokenResponseDto: ResponseDto {
    val accessToken: LoginAccessToken
}

object EmptyResponseDto: ResponseDto {
    override fun containsNullField() = false
}