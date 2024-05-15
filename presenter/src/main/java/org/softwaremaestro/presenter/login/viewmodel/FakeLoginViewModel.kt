package org.softwaremaestro.presenter.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.softwaremaestro.domain.fake_login.entity.Role
import org.softwaremaestro.domain.fake_login.result.NetworkSuccess
import org.softwaremaestro.domain.fake_login.usecase.FakeAutoLoginUseCase
import org.softwaremaestro.presenter.util.UIState
import javax.inject.Inject

@HiltViewModel
class FakeLoginViewModelModel @Inject constructor(
    private val fakeAutoLoginUseCase: FakeAutoLoginUseCase
): ViewModel() {
    private val _role: MutableStateFlow<UIState<Role>> = MutableStateFlow(UIState.Empty)
    val role: StateFlow<UIState<Role>> get() = _role

    fun autoLogin() {
        viewModelScope.launch {
            _role.value = UIState.Loading

            val result = fakeAutoLoginUseCase()

            _role.value = if (result is NetworkSuccess && result.dto != null) {
                UIState.Success(result.dto!!)
            } else {
                UIState.Failure
            }
        }
    }
}