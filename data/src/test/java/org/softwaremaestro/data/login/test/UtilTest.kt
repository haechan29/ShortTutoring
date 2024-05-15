package org.softwaremaestro.data.login.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.softwaremaestro.data.fake_login.dto.ResponseDto
import org.softwaremaestro.domain.fake_login.result.NetworkFailure
import org.softwaremaestro.domain.fake_login.result.NetworkResult
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.domain.fake_login.util.attemptUntilSuccess
import org.softwaremaestro.domain.fake_login.util.containsNullField

class UtilTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("containsNullField 테스트") {
        data class ClassWithThreeNullableField(val field1: Any?, val field2: Any?, val field3: Any?)

        test("인스턴스가 Null 필드를 하나도 포함하지 않으면 false를 반환한다") {
            val anyArb = with (Arb) { choice(int(), string(), boolean(), double(), long()) }

            checkAll(anyArb, anyArb, anyArb) { nonNull1, nonNull2, nonNull3 ->
                val instanceWithNonNulls = ClassWithThreeNullableField(nonNull1, nonNull2, nonNull3)
                containsNullField(instanceWithNonNulls) shouldBe false
            }
        }

        test("인스턴스가 Null 필드를 하나라도 포함하면") {
            val anyNullableArb = with (Arb) { choice(int(), string(), boolean(), double(), long(), constant(null)) }

            checkAll(anyNullableArb, anyNullableArb, anyNullableArb) { nullable1, nullable2, nullable3 ->
                val instanceWithNullables = ClassWithThreeNullableField(nullable1, nullable2, nullable3)

                if (nullable1 == null || nullable2 == null || nullable3 == null)
                    containsNullField(instanceWithNullables) shouldBe true
            }
        }
    }

    context("attemptUntilSuccess 테스트") {
        context("함수가 성공을 반환하면") {
            val f = spyk({ mockk<NetworkSuccess<ResponseDto>>(relaxed = true) })

            attemptUntilSuccess(3) { f() }

            test("함수를 호출하지 않는다") {
                verify(exactly = 1) { f() }
            }
        }

        context("함수가 실패를 반환하면") {
            val f = spyk({ mockk<NetworkFailure>(relaxed = true) })
            val attemptLimit = 3

            attemptUntilSuccess(attemptLimit) { f() }

            test("함수를 설정한 횟수만큼 다시 호출한다") {
                verify(exactly = attemptLimit + 1) { f() }
            }
        }

        context("함수가 실패를 반환한 후에 성공을 반환하면") {
            val f = mockk<() -> NetworkResult<ResponseDto>>(relaxed = true)
            every { f.invoke() } returnsMany listOf(
                mockk<NetworkFailure>(relaxed = true),
                mockk<NetworkSuccess<ResponseDto>>(relaxed = true)
            )

            attemptUntilSuccess(3) { f() }

            test("더이상 함수를 호출하지 않는다") {
                verify(exactly = 2) { f() }
            }
        }
    }
})