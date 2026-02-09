package com.travelotef.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelotef.app.data.repository.AuthRepository
import com.travelotef.app.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>?>(null)
    val loginState: StateFlow<UiState<Unit>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<Unit>?>(null)
    val registerState: StateFlow<UiState<Unit>?> = _registerState.asStateFlow()

    private val _resetState = MutableStateFlow<UiState<Unit>?>(null)
    val resetState: StateFlow<UiState<Unit>?> = _resetState.asStateFlow()

    val isLoggedIn: Boolean get() = authRepository.isLoggedIn

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("אנא מלא את כל השדות")
            return
        }
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            authRepository.login(email, password).fold(
                onSuccess = { _loginState.value = UiState.Success(Unit) },
                onFailure = { _loginState.value = UiState.Error(it.message ?: "שגיאה בהתחברות") }
            )
        }
    }

    fun register(email: String, password: String, confirmPassword: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _registerState.value = UiState.Error("אנא מלא את כל השדות")
            return
        }
        if (password != confirmPassword) {
            _registerState.value = UiState.Error("הסיסמאות אינן תואמות")
            return
        }
        if (password.length < 6) {
            _registerState.value = UiState.Error("הסיסמה חייבת להכיל לפחות 6 תווים")
            return
        }
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            authRepository.register(email, password, name).fold(
                onSuccess = { _registerState.value = UiState.Success(Unit) },
                onFailure = { _registerState.value = UiState.Error(it.message ?: "שגיאה בהרשמה") }
            )
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _resetState.value = UiState.Error("אנא הכנס כתובת מייל")
            return
        }
        viewModelScope.launch {
            _resetState.value = UiState.Loading
            authRepository.resetPassword(email).fold(
                onSuccess = { _resetState.value = UiState.Success(Unit) },
                onFailure = { _resetState.value = UiState.Error(it.message ?: "שגיאה בשליחת מייל") }
            )
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
