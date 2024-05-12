package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.softwaremaestro.data.mylogin.util.SyncQueue

class SyncQueueTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    var invoked = 0

    val f = suspend {
        delay(100)
        invoked++
    }

    val syncQueue = spyk(object: SyncQueue<Int>(f) {}, recordPrivateCalls = true)

    context("함수를 여러 번 호출해도") {
        runTest {
            repeat(10) {
                launch {
                    syncQueue.sync()
                }
            }
        }

        test("함수는 한 번만 호출된다") {
            invoked shouldBe 1
        }
    }
})