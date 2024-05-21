package org.softwaremaestro.data.login.login2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec

class LoginRepositoryTest: FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf


    xtest("로그인 버튼을 누르면 로그인을 시작한다") {

    }

    xtest("로그인이 실패하면 사용자에게 로그인이 실패했음을 알린다") {

    }

    xcontext("로그인이 성공하면 사용자는 홈 화면으로 이동한다") {
        test("학생 회원은 학생 홈 화면으로 이동한다") {
        }

        test("선생님 회원은 선생님 홈 화면으로 이동한다") {
        }
    }

    xtest("Splash Activity에 진입하면 자동 로그인을 시작한다") {
        // TODO()
    }

    xcontext("토큰의 유효성을 주기적으로 확인한다") {}
})