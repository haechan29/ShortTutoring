package org.softwaremaestro.presenter.teacher_home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.softwaremaestro.domain.answer_upload.entity.AnswerUploadResultVO
import org.softwaremaestro.domain.answer_upload.entity.AnswerUploadVO
import org.softwaremaestro.domain.answer_upload.usecase.AnswerUploadUseCase
import org.softwaremaestro.domain.common.BaseResult
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor(private val answerUploadUseCase: AnswerUploadUseCase): ViewModel() {

    private val _answer: MutableLiveData<AnswerUploadResultVO> = MutableLiveData()
    val answer: LiveData<AnswerUploadResultVO> get() = _answer

    fun uploadAnswer(answerUploadVO: AnswerUploadVO) {
        viewModelScope.launch {
            answerUploadUseCase.execute(answerUploadVO)
                .catch { exception ->
                    // Todo: 추후에 에러 어떻게 처리할지 생각해보기
                    Log.d("Error", exception.message.toString())
                }
                .collect { result ->
                    when (result) {
                        is BaseResult.Success -> _answer.value = result.data
                        is BaseResult.Error -> Log.d("Error", result.toString())
                    }
                }
        }
    }
}