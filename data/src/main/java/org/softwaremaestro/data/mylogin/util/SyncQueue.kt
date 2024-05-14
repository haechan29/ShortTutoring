package org.softwaremaestro.data.mylogin.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.softwaremaestro.domain.mylogin.entity.dto.EmptyResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult

class NetworkSyncQueue {
    companion object {
        private var objectSyncQueue: SyncQueue<NetworkResult<EmptyResponseDto>>? = null

        suspend fun sync(f: suspend () -> NetworkResult<EmptyResponseDto>): NetworkResult<EmptyResponseDto> {
            if (objectSyncQueue == null) {
                objectSyncQueue = SyncQueue.getObject(f, ::clear)
            }
            return objectSyncQueue!!.sync()
        }

        private fun clear() {
            objectSyncQueue = null
        }
    }
}

abstract class SyncQueue<R>(private val f: suspend () -> R, private val onFinished: () -> Unit = {}) {
    companion object {
        fun <R> getObject(f: suspend () -> R, onFinished: () -> Unit = {}): SyncQueue<R> {
            return object: SyncQueue<R>(f, onFinished) {}
        }
    }

    private var pendingCount = 0
    private val mutex = Mutex()

    private var result: R? = null

    suspend fun sync(): R {
        val temp: R

        mutex.withLock {
            pendingCount++

            if (result == null) {
                result = f()
            }

            temp = result!!
        }

        mutex.withLock {
            pendingCount--
            if (pendingCount == 0) {
                result = null
                onFinished()
            }
        }

        return temp
    }
}