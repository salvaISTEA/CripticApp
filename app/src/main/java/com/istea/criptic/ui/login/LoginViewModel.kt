package com.istea.criptic.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.istea.criptic.data.LoginRepository
import com.istea.criptic.data.Result

import com.istea.criptic.R

// Modelo de la vista del login
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    // Formularios del login y resultado
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // Coloca los resultados de intento de login en el LoginResult
    fun login(username: String, password: String) {
        val result = loginRepository.login(username, password)
        if (result is Result.Success) {
            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    // Coloca observadores para validar los datos de login
    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // Valida el nombre de usuario si es regular o es un email
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // Valida si la contrasena tiene 5 o mas caracteres
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}