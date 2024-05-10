package org.softwaremaestro.domain.mylogin.entity

sealed interface Result
interface Ok: Result
interface Failure: Result