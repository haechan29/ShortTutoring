package org.softwaremaestro.presenter.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.softwaremaestro.presenter.login.TokenException
import org.softwaremaestro.domain.login.entity.LoginToken
import org.softwaremaestro.presenter.util.UIState
import javax.inject.Inject

@HiltViewModel
class MyLoginViewModelModel @Inject constructor(): ViewModel() {

    private val _role: MutableSharedFlow<UIState<String>> = MutableSharedFlow()
    val role: SharedFlow<UIState<String>> get() = _role

    fun myLogin() {
        viewModelScope.launch {
            flowWithUIState {
                val token = getToken()
                getRole(token)
            }.collect { role ->
                _role.emit(role)
            }
        }
    }

    private suspend fun getToken(): LoginToken {
        return flow<LoginToken> {
            getTokenFromLocalDB()
        }
            .flowOn(Dispatchers.IO)
            .retryWhen { cause, attempt ->
                requestToken()
                cause is TokenException && attempt < 3
            }
            .catch { e ->
                throw TokenException()
            }
            .first()
    }

    private suspend fun getTokenFromLocalDB(): LoginToken = coroutineScope {
        TOKEN_FROM_LOCAL_DB
    }

    private suspend fun requestToken() = coroutineScope {
        val token: LoginToken = requestTokenToServer()
        insertToLocalDB("name to insert token", token)
    }

    private fun requestTokenToServer(): LoginToken {
        return TOKEN_FROM_SERVER
    }

    private fun insertToLocalDB(key: String, token: LoginToken) {

    }

    private fun getRole(token: LoginToken): String {
        return token.content
    }

    private fun <R> flowWithUIState(f: suspend () -> R): Flow<UIState<R>> {
        return flow {
            emit(UIState.Loading)

            try {
                emit(UIState.Success(f()))
            } catch (e: Exception) {
                emit(UIState.Failure)
            }
        }
    }

    companion object {
        val TOKEN_FROM_LOCAL_DB  = LoginToken("token from local db", true)
        val TOKEN_FROM_SERVER    = LoginToken("token from server"  , true)
    }
}