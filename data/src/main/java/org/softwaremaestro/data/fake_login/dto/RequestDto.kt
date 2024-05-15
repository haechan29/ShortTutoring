package org.softwaremaestro.data.fake_login.dto

import org.softwaremaestro.domain.fake_login.entity.LoginAccessToken
import org.softwaremaestro.domain.fake_login.entity.LoginRefreshToken
import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.entity.Validatable

interface RequestDto

interface IssueLoginTokenRequestDto: RequestDto

data class IssueAccessTokenRequestDto(
    private val refreshToken: LoginRefreshToken
): IssueLoginTokenRequestDto

class IssueRefreshTokenRequestDto(
    private val id: String, private val password: String
): IssueLoginTokenRequestDto, Validatable {
    override fun isValid(): Boolean {
        return !(id.isEmpty() || password.isEmpty())
    }
}

object EmptyRequestDto: RequestDto
