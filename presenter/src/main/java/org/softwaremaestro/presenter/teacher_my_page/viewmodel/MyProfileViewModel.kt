package org.softwaremaestro.presenter.teacher_my_page.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.softwaremaestro.domain.common.BaseResult
import org.softwaremaestro.domain.my_profile_get.entity.MyProfileGetResponseVO
import org.softwaremaestro.domain.my_profile_get.usecase.MyProfileGetUseCase
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(private val myProfileGetUseCase: MyProfileGetUseCase) :
    ViewModel() {

    private val _myProfile: MutableLiveData<MyProfileGetResponseVO> = MutableLiveData()
    val myProfile: LiveData<MyProfileGetResponseVO> get() = _myProfile

    private val _numOfFollower: MutableLiveData<Int> = MutableLiveData()
    val numOfFollower: LiveData<Int> get() = _numOfFollower

    fun getMyProfile() {
        viewModelScope.launch {
            myProfileGetUseCase.execute()
                .catch { exception ->
                    // Todo: 추후에 에러 어떻게 처리할지 생각해보기
                    Log.d("Error", exception.message.toString())
                }
                .collect { result ->
                    when (result) {
                        is BaseResult.Success -> {
                            result.data.let {
                                _myProfile.postValue(it)
                                _numOfFollower.postValue(it.followers?.size)
                            }
                        }

                        is BaseResult.Error -> Log.d("Error", result.toString())
                    }
                }
        }
    }

    fun addOne() = _numOfFollower.postValue(_numOfFollower.value!! + 1)

    fun minusOne() = _numOfFollower.postValue(_numOfFollower.value!! - 1)
}