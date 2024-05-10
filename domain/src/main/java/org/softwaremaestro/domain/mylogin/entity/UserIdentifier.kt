package org.softwaremaestro.domain.mylogin.entity

interface UserIdentifier {
    suspend fun identifyUser(): Boolean
}