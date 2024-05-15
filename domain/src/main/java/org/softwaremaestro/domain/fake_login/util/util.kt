package org.softwaremaestro.domain.fake_login.util

import org.softwaremaestro.domain.fake_login.result.AuthFailure
import org.softwaremaestro.domain.fake_login.result.AuthResult
import org.softwaremaestro.domain.fake_login.result.AuthSuccess
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun <T> NetworkResult<T>.nullIfSuccess(): NetworkFailure? {
    return when (this) {
        is NetworkSuccess<T> -> null
        is NetworkFailure -> this
    }
}

fun AuthResult.nullIfSuccess(): AuthFailure? {
    return when (this) {
        is AuthSuccess -> null
        is AuthFailure -> this
    }
}

fun <T> NetworkResult<T>.dtoOrNull(): T? {
    return when (this) {
        is NetworkSuccess<T> -> dto
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

suspend fun <T> attemptUntilSuccess(
    attemptLimit: Int,
    vararg acceptableFailures: NetworkFailure = emptyArray(),
    f: suspend () -> NetworkResult<T>
): NetworkResult<T> {
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