package org.softwaremaestro.data.mylogin.util

import org.softwaremaestro.domain.mylogin.entity.result.AuthFailure
import org.softwaremaestro.domain.mylogin.entity.result.AuthResult
import org.softwaremaestro.domain.mylogin.entity.result.AuthSuccess
import org.softwaremaestro.domain.mylogin.entity.result.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.result.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun <Dto: ResponseDto> NetworkResult<Dto>.nullIfSuccess(): NetworkFailure? {
    return when (this) {
        is NetworkSuccess<Dto> -> null
        is NetworkFailure -> this
    }
}

fun AuthResult.nullIfSuccess(): AuthFailure? {
    return when (this) {
        is AuthSuccess -> null
        is AuthFailure -> this
    }
}

fun <Dto: ResponseDto> NetworkResult<Dto>.dtoOrNull(): Dto? {
    return when (this) {
        is NetworkSuccess<Dto> -> dto
        is NetworkFailure -> null
    }
}

fun containsNullField(instance: Any): Boolean {
    val kClass = instance::class
    val properties = kClass.memberProperties

    for (property in properties) {
        property.isAccessible = true
        (property as? kotlin.reflect.KProperty1<Any, *>)?.get(instance) ?: return true
    }
    return false
}

// TODO("token issuer에 있는 테스트 가져오기)
suspend fun <Dto: ResponseDto> attemptUntilSuccess(
    attemptLimit: Int,
    vararg acceptableFailures: NetworkFailure = emptyArray(),
    f: suspend () -> NetworkResult<Dto>
): NetworkResult<Dto> {
    var attempt = 0
    var result = f()

    while (true) {
        if (attempt >= attemptLimit) break
        if (result is NetworkSuccess || result in acceptableFailures) break

        result = f()
        attempt++
    }

    return result
}