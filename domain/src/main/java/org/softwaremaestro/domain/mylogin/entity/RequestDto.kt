package org.softwaremaestro.domain.mylogin.entity

interface RequestDto
interface LoginRequestDto: RequestDto, Validatable {
    override fun isValid(): Boolean
}

interface IssueTokenRequestDto: RequestDto