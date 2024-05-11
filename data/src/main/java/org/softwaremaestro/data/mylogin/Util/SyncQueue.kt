package org.softwaremaestro.data.mylogin.Util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncQueue<R> {
    private var pendingCount = 0
    private val mutex = Mutex()

    private var result: R? = null

    suspend fun sync(f: suspend () -> R): R {
        val temp: R

        mutex.withLock {
            pendingCount++

            if (result == null) {
                result = f()
            }
        }

        temp = result!!

        mutex.withLock {
            pendingCount--
            if (pendingCount == 0) {
                result = null
            }
        }

        return temp
    }
}