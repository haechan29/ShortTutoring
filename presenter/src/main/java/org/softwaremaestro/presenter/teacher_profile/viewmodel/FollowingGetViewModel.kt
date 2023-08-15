package org.softwaremaestro.presenter.teacher_profile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.softwaremaestro.domain.common.BaseResult
import org.softwaremaestro.domain.following_get.entity.FollowingGetResponseVO
import org.softwaremaestro.domain.following_get.usecase.FollowingGetUseCase
import javax.inject.Inject

@HiltViewModel
class FollowingGetViewModel @Inject constructor(private val followingGetUseCase: FollowingGetUseCase) :
    ViewModel() {

    private val _following: MutableLiveData<List<FollowingGetResponseVO>> = MutableLiveData()
    val following: LiveData<List<FollowingGetResponseVO>> get() = _following

    fun getFollowing(userId: String) {
        viewModelScope.launch {
            followingGetUseCase.execute(userId)
                .catch { exception ->
                    // Todo: 추후에 에러 어떻게 처리할지 생각해보기
                    Log.d("Error", exception.message.toString())
                }
                .collect { result ->
                    when (result) {
                        is BaseResult.Success -> _following.postValue(result.data)
                        is BaseResult.Error -> Log.d("Error", result.toString())
                    }
                }
        }
    }
}