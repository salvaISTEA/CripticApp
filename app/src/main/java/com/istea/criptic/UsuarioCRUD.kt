package com.istea.criptic

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.math.BigInteger
import java.security.MessageDigest

// CRUD para facilitar el ABM de usuarios
class UsuarioCRUD(context: Context) {

    // Inicializamos informacion relevante
    private var helper: DataBaseHelper? = null
    private var tableName: String = UsuarioContract.Companion.Entrada.NOMBRE_TABLA
    private var tableVersion: Int = UsuarioContract.VERSION

    init {
        // Obtenemos una instancia del helper de la DB
        helper = DataBaseHelper(context, tableName, tableVersion)
    }

    // Codifica strings usando el algoritmo md5
    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    // Anade un nuevo usuario siguiendo el item pasado
    fun anadirNuevoUsuario(item: Usuario) {
        //Abrir la DB en modo escritura
        val db:SQLiteDatabase = helper?.writableDatabase!!

        //Mapeo de las columnas con valores a insertar
        val values = ContentValues()
        values.put(UsuarioContract.Companion.Entrada.COLUMNA_NOMBRE_DE_USUARIO, item.nombreUsuario)
        values.put(UsuarioContract.Companion.Entrada.COLUMNA_EMAIL, item.email)
        values.put(UsuarioContract.Companion.Entrada.COLUMNA_CONTRASENA, md5(item.password!!))

        //Insertar una nueva fila en la tabla
        val newRowId = db.insert(UsuarioContract.Companion.Entrada.NOMBRE_TABLA, null, values)

        //Cerrar la conexion
        db.close()
    }

    // Trata de buscar el usuario en la DB con el nombre pasado
    fun getUsuario(nombreUsuario:String): Usuario {
        var item: Usuario? = null

        //Abrir DB en modo lectura
        val db: SQLiteDatabase = helper?.readableDatabase!!

        //Especificar columnas que quiero consultar
        val columnas = arrayOf(
            UsuarioContract.Companion.Entrada.COLUMNA_ID,
            UsuarioContract.Companion.Entrada.COLUMNA_NOMBRE_DE_USUARIO,
            UsuarioContract.Companion.Entrada.COLUMNA_EMAIL,
            UsuarioContract.Companion.Entrada.COLUMNA_CONTRASENA
        )

        //Crear un cursos para recorrer la tabla
        val c:Cursor = db.query(
            UsuarioContract.Companion.Entrada.NOMBRE_TABLA,
            columnas,
            " nombre_usuario = ?",
            arrayOf(nombreUsuario),
            null,
            null,
            null
        )

        while (c.moveToNext()) {
            item = Usuario(c.getString(c.getColumnIndexOrThrow(UsuarioContract.Companion.Entrada.COLUMNA_NOMBRE_DE_USUARIO)),
                c.getString(c.getColumnIndexOrThrow(UsuarioContract.Companion.Entrada.COLUMNA_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(UsuarioContract.Companion.Entrada.COLUMNA_CONTRASENA))
            )
        }
        c.close()

        return item!!
    }

    // Trata de obtener el usuario en la DB usando el email
    fun getUsuarioByEmail(email:String): Usuario {
        var item: Usuario? = null

        //Abrir DB en modo lectura
        val db: SQLiteDatabase = helper?.readableDatabase!!

        //Especificar columnas que quiero consultar
        val columnas = arrayOf(
            UsuarioContract.Companion.Entrada.COLUMNA_ID,
            UsuarioContract.Companion.Entrada.COLUMNA_NOMBRE_DE_USUARIO,
            UsuarioContract.Companion.Entrada.COLUMNA_EMAIL,
            UsuarioContract.Companion.Entrada.COLUMNA_CONTRASENA
        )

        //Crear un cursos para recorrer la tabla
        val c:Cursor = db.query(
            UsuarioContract.Companion.Entrada.NOMBRE_TABLA,
            columnas,
            " email = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        while (c.moveToNext()) {
            item = Usuario(c.getString(c.getColumnIndexOrThrow(UsuarioContract.Companion.Entrada.COLUMNA_NOMBRE_DE_USUARIO)),
                c.getString(c.getColumnIndexOrThrow(UsuarioContract.Companion.Entrada.COLUMNA_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(UsuarioContract.Companion.Entrada.COLUMNA_CONTRASENA))
            )
        }
        c.close()

        return item!!
    }
}