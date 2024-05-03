package org.softwaremaestro.domain.mylogin.entity

interface Api {
    suspend fun send(dto: RequestDto)
}

interface LoginApi: Api