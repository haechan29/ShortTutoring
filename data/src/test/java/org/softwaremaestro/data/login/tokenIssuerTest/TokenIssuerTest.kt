package org.softwaremaestro.data.login.tokenIssuerTest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.spyk
import org.softwaremaestro.domain.mylogin.entity.TokenIssuer

class TokenIssuerTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val tokenIssuer = spyk<TokenIssuer>()

    context("액세스 토큰이 검증에 실패하면") {
        context("액세스 토큰 발급을 요청한다") {

        }
    }

    context("리프레시 토큰이 검증에 실패하면") {
        context("액세스 토큰과 리프레시 토큰 발급을 요청한다") {

        }
    }
    
    context("토큰 발급 응답을 받았을 때") {
        context("토큰 발급 결과를 반환한다") {

        }

        context("토큰 발급이 실패하면") {
            context("토큰 발급을 재시도한다") {

            }

            context("토큰 발급을 3회 이상 시도했다면") {
                context("토큰 발급을 실패 처리한다") {

                }
            }
        }
    }

    context("토큰이 발급되면") {
        context("토큰을 저장한다") {

        }
    }
})