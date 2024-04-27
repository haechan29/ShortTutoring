package org.softwaremaestro.presenter.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.softwaremaestro.presenter.databinding.ActivitySplashBinding
import org.softwaremaestro.presenter.login.viewmodel.LoginViewModel
import org.softwaremaestro.presenter.student_home.StudentHomeActivity
import org.softwaremaestro.presenter.teacher_home.TeacherHomeActivity
import org.softwaremaestro.presenter.util.UIState

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val loginViewModel: LoginViewModel by viewModels()

    private var chatId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPendingIntent()

        autoLogin()
    }

    private fun getPendingIntent() {
        val args = intent.extras
        args?.apply {
            try {
                chatId = getString(APP_LINK_ARGS_CHAT_ID)
            } catch (e: Exception) {
                Log.w(this@SplashActivity::class.java.name, "getPendingIntent: $e")
            }
        }
    }


    private fun autoLogin() {
        loginViewModel.getRoleFromLocalDB()

        loginViewModel.role.observe(this) {
            if (it is UIState.Success) {
                loginViewModel.registerFCMToken()
            }
            startTargetActivity(it._data)
        }
    }

    private fun startTargetActivity(role: String? = null) {
        val targetActivityClass = when (role) {
            "student" ->    StudentHomeActivity::class.java
            "teacher" ->    TeacherHomeActivity::class.java
            else ->         LoginActivity::class.java
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