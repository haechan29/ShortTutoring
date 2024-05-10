package org.softwaremaestro.domain.mylogin.entity

interface ResponseDto

interface LoginResponseDto: ResponseDto {
    val role: Role?
}

interface IssueTokenResponseDto: ResponseDto {
    val accessToken: LoginAccessToken?
    val refreshToken: LoginRefreshToken?
}

interface LocalTokenResponseDto: ResponseDto {
    val accessToken: LoginAccessToken
}

object EmptyResponseDto: ResponseDto