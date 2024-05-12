package org.softwaremaestro.data.mylogin.util

import org.softwaremaestro.domain.mylogin.entity.Failure
import org.softwaremaestro.domain.mylogin.entity.Result
import org.softwaremaestro.domain.mylogin.entity.NetworkFailure
import org.softwaremaestro.domain.mylogin.entity.NetworkSuccess
import org.softwaremaestro.domain.mylogin.entity.NetworkResult
import org.softwaremaestro.domain.mylogin.entity.ResponseDto


fun <Dto: ResponseDto> NetworkResult<Dto>.nullIfOk(): NetworkFailure? {
    return when (this) {
        is NetworkSuccess<Dto> -> null
        is NetworkFailure -> this
    }
}

fun <T> Result<T>.nullIfOk(): Failure<T>? {
    return when (this) {
        is Failure<T> -> this
        else -> null
    }
}

fun <Dto: ResponseDto> NetworkResult<Dto>.dtoOrNull(): Dto? {
    return when (this) {
        is NetworkSuccess<Dto> -> dto
        is NetworkFailure -> null
    }
}

suspend fun <Dto: ResponseDto> attemptUntil(attemptLimit: Int, f: suspend () -> NetworkResult<Dto>): NetworkResult<Dto> {
    var attempt = 0
    var result = f()
    while (attempt < attemptLimit && result is NetworkFailure) {
        result = f()
        attempt++
    }
    return result
}