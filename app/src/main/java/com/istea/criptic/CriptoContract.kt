package com.istea.criptic

import android.provider.BaseColumns

// Representacion de la tabla de criptos
class CriptoContract {
    companion object {
        const val VERSION = 1

        class Entrada: BaseColumns{

            companion object {
                const val NOMBRE_TABLA = "precio_criptos"
                const val COLUMNA_ID = "id"
                const val COLUMNA_RECURSO = "recurso"
                const val COLUMNA_PRECIO_RECURSO = "precio_recurso"
            }
        }
    }
}