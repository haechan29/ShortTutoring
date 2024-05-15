package org.softwaremaestro.presenter.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.softwaremaestro.domain.fake_login.entity.Role
import org.softwaremaestro.presenter.databinding.ActivitySplashBinding
import org.softwaremaestro.presenter.login.viewmodel.FakeLoginViewModelModel
import org.softwaremaestro.presenter.student_home.StudentHomeActivity
import org.softwaremaestro.presenter.teacher_home.TeacherHomeActivity
import org.softwaremaestro.presenter.util.UIState

@AndroidEntryPoint
class FakeSplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private val fakeLoginViewModel: FakeLoginViewModelModel by viewModels()

    private var chatId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPendingIntent()

        fakeAutoLogin()
    }

    private fun getPendingIntent() {
        val args = intent.extras
        args?.apply {
            try {
                chatId = getString(APP_LINK_ARGS_CHAT_ID)
            } catch (e: Exception) {
                Log.w(this@FakeSplashActivity::class.java.name, "getPendingIntent: $e")
            }
        }
    }

    private fun fakeAutoLogin() {
        fakeLoginViewModel.autoLogin()

        lifecycleScope.launch {
            fakeLoginViewModel.role.collect { roleState ->
                if (roleState is UIState.Success) {
                    startTargetActivity(roleState.data)
                }
            }
        }
    }

    private fun startTargetActivity(role: Role) {
        val targetActivityClass = when (role) {
            Role.STUDENT -> StudentHomeActivity::class.java
            Role.TEACHER -> TeacherHomeActivity::class.java
        }

        val intent = Intent(this, targetActivityClass).apply {
            chatId?.let { putExtra(APP_LINK_ARGS_CHAT_ID, it) }
        }
        startActivity(intent)
    }

    companion object {
        const val APP_LINK_ARGS_CHAT_ID = "chattingId"
    }
}