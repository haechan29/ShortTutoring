package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.LoginToken

interface TokenStorage<Token: LoginToken> {
    suspend fun save(token: Token)
    suspend fun load(): Token?
    suspend fun clear()
}