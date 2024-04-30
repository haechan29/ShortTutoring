package org.softwaremaestro.data.mylogin

sealed interface Token
interface AccessToken: Token
interface RefreshToken: Token