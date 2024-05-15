package org.softwaremaestro.domain.fake_login.result

interface Server

sealed interface ServerResult: Result<Server>

interface ServerSuccess: ServerResult, Success<Server>
interface ServerFailure: ServerResult, Failure<Server>