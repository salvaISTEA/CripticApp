package com.istea.criptic

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Facilita las operaciones que se hagan contra la DB
class DataBaseHelper(context: Context, tableName: String, tableVersion: Int):
    SQLiteOpenHelper(
        context,
        tableName,
        null,
        tableVersion,
    )
{

    // Crea las consultas necesarias para levantar y tirar las tablas necesarias
    companion object {
        val CREAR_TABLA_RECURSOS = "CREATE TABLE ${CriptoContract.Companion.Entrada.NOMBRE_TABLA}" +
                "(${CriptoContract.Companion.Entrada.COLUMNA_ID} TEXT PRIMARY KEY, " +
                "${CriptoContract.Companion.Entrada.COLUMNA_RECURSO} TEXT, " +
                "${CriptoContract.Companion.Entrada.COLUMNA_PRECIO_RECURSO} INT )"

        val REMOVER_TABLA_RECURSOS = "DROP TABLE IF EXISTS ${CriptoContract.Companion.Entrada.NOMBRE_TABLA}"

        val CREAR_TABLA_USUARIOS = "CREATE TABLE ${UsuarioContract.Companion.Entrada.NOMBRE_TABLA}" +
                "(${UsuarioContract.Companion.Entrada.COLUMNA_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${UsuarioContract.Companion.Entrada.COLUMNA_NOMBRE_DE_USUARIO} TEXT UNIQUE, " +
                "${UsuarioContract.Companion.Entrada.COLUMNA_EMAIL} TEXT UNIQUE, " +
                "${UsuarioContract.Companion.Entrada.COLUMNA_CONTRASENA} TEXT )"

        val REMOVER_TABLA_USUARIOS = "DROP TABLE IF EXISTS ${UsuarioContract.Companion.Entrada.NOMBRE_TABLA}"
    }

    // Llamada al crear el helper para inicializar las tablas recursos
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREAR_TABLA_RECURSOS)
        db?.execSQL(CREAR_TABLA_USUARIOS)
    }

    // Llamada al cambiar las versiones de las tablas anadidas
    override fun onUpgrade(db: SQLiteDatabase?, i: Int, i2: Int) {
        db?.execSQL(REMOVER_TABLA_RECURSOS)
        db?.execSQL(REMOVER_TABLA_USUARIOS)
        onCreate(db)
    }

}