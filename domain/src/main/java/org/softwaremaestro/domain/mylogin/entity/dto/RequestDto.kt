package org.softwaremaestro.domain.mylogin.entity.dto

import org.softwaremaestro.domain.mylogin.entity.Validatable

interface RequestDto
interface LoginRequestDto: RequestDto, Validatable {
    override fun isValid(): Boolean
}

object EmptyRequestDto: RequestDto