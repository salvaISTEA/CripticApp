package com.istea.criptic.ui.login

// Tipo de los datos recibidos del resultado del login
data class LoginResult (
     val success:LoggedInUserView? = null,
     val error:Int? = null
)