package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.softwaremaestro.data.mylogin.SyncQueue

class SyncQueueTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    beforeEach { unmockkAll() }

    val syncQueue = spyk(SyncQueue<Int>(), recordPrivateCalls = true)

    context("함수를 여러 번 호출해도").config(coroutineTestScope = true) {
        var invoked = 0

        val f = suspend {
            delay(100)
            invoked++
        }

        val jobs = mutableListOf<Job>()

        repeat(10) {
            val job = launch {
                syncQueue.sync { f() }
            }
            jobs.add(job)
        }

        jobs.joinAll()

        test("함수는 한 번만 호출된다") {
            invoked shouldBe 1
        }
    }

    afterEach { unmockkAll() }
})