package com.istea.criptic

import android.provider.BaseColumns

class UsuarioContract {

    companion object {

        const val VERSION = 1

        class Entrada: BaseColumns{
            companion object {
                const val NOMBRE_TABLA = "usuarios"
                const val COLUMNA_ID = "id"
                const val COLUMNA_NOMBRE_DE_USUARIO = "nombre_usuario"
                const val COLUMNA_CONTRASENA = "password"
                const val COLUMNA_EMAIL = "email"
            }
        }
    }
}