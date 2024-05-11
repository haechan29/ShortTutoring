package org.softwaremaestro.data.mylogin.Util

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