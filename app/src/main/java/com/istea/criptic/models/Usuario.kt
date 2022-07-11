package com.istea.criptic.models

// Representacion de un usuario con los atributos que usamos
class Usuario(nombreUsuario: String, email: String, password: String) {
    var nombreUsuario: String? = null
    var password: String? = null
    var email: String? = null

    init {
        this.nombreUsuario = nombreUsuario
        this.password = password
        this.email = email
    }
}
