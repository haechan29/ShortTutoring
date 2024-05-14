package org.softwaremaestro.domain.mylogin.entity.result

interface Server

sealed interface ServerResult: Result<Server>

interface ServerSuccess: ServerResult, Success<Server>
interface ServerFailure: ServerResult, Failure<Server>