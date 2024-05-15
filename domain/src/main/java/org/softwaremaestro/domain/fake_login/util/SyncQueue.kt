package org.softwaremaestro.domain.fake_login.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.softwaremaestro.domain.fake_login.result.NetworkResult

class NetworkSyncQueue {
    companion object {
        private var objectSyncQueue: SyncQueue<NetworkResult<Unit>>? = null

        suspend fun sync(f: suspend () -> NetworkResult<Unit>): NetworkResult<Unit> {
            if (objectSyncQueue == null) {
                objectSyncQueue = SyncQueue.getObject(f, Companion::clear)
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