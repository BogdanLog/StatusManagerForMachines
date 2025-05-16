package ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import data.prefs.UserPrefs
import data.repository.AuthRepositoryRemote
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val login: String) : AuthState()
    data object Error : AuthState()
}

class AuthViewModel(
    private val repo: AuthRepositoryRemote,
    private val prefs: UserPrefs
) : ViewModel() {
    init {
        viewModelScope.launch {
            prefs.currentLogin.collect { saved ->
                if (!saved.isNullOrEmpty()) {
                    _state.value = AuthState.Authenticated(saved)
                    loginText.value = saved
                }
            }
        }
    }

    val loginText = MutableStateFlow("")
    val passText = MutableStateFlow("")
    private val _state = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val state = _state.asStateFlow()

    fun onLoginChange(text: String) {
        loginText.value = text
    }

    fun onPassChange(text: String) {
        passText.value = text
    }

    fun attemptLogin() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            if (repo.login(loginText.value, passText.value)) {
                prefs.saveLogin(loginText.value)
                _state.value = AuthState.Authenticated(loginText.value)
            } else {
                _state.value = AuthState.Error
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _state.value = AuthState.Unauthenticated
            loginText.value = ""
            passText.value = ""
        }
    }
}

class AuthViewModelFactory(
    private val repo: AuthRepositoryRemote,
    private val prefs: UserPrefs
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repo, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}