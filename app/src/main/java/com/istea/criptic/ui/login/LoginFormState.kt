package com.istea.criptic.ui.login

// Valida la informacion del Login Form
data class LoginFormState (val usernameError: Int? = null,
                      val passwordError: Int? = null,
                      val isDataValid: Boolean = false)