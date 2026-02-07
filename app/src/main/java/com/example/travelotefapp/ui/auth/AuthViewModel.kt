package com.example.travelotefapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelotefapp.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel :  ViewModel() {
    
    private val repository = AuthRepository()
    
    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState
    
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            repository.signInWithEmail(email, password)
                .onSuccess {
                    _authState.value = AuthState.Success
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(
                        getErrorMessage(exception)
                    )
                }
        }
    }
    
    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            repository.signUpWithEmail(email, password)
                .onSuccess {
                    _authState.value = AuthState.Success
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(
                        getErrorMessage(exception)
                    )
                }
        }
    }
    
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            repository.signInWithGoogle(account)
                .onSuccess {
                    _authState.value = AuthState.Success
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(
                        getErrorMessage(exception)
                    )
                }
        }
    }
    
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            repository.resetPassword(email)
                .onSuccess {
                    _authState.value = AuthState.Error("נשלח אימייל לאיפוס סיסמה ✅")
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(
                        getErrorMessage(exception)
                    )
                }
        }
    }
    
    private fun getErrorMessage(exception:  Throwable): String {
        return when {
            exception.message?.contains("There is no user record") == true -> 
                "משתמש לא קיים במערכת"
            exception.message?.contains("password is invalid") == true -> 
                "סיסמה שגויה"
            exception.message?.contains("email address is already in use") == true -> 
                "האימייל כבר רשום במערכת"
            exception.message?.contains("network") == true -> 
                "בעיית חיבור לאינטרנט"
            exception.message?.contains("badly formatted") == true -> 
                "אימייל לא תקין"
            else -> exception.message ?: "שגיאה לא ידועה"
        }
    }
}