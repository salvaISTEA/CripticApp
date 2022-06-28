package com.istea.criptic

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.math.BigInteger
import java.security.MessageDigest

// CRUD para facilitar el ABM de precios de criptos
class PrecioCriptoCRUD(context: Context) {

    // Inicializamos informacion relevante
    private var helper:DataBaseHelper? = null
    private var tableName: String = CriptoContract.Companion.Entrada.NOMBRE_TABLA
    private var tableVersion: Int = CriptoContract.VERSION

    init {
        // Obtenemos una instancia del helper de la DB
        helper = DataBaseHelper(context, tableName, tableVersion)
    }

    // Funcion para agregar una nueva cripto a la DB
    fun anadirNuevaCripto(item: Cripto) {
        //Abrir la DB en modo escritura
        val db:SQLiteDatabase = helper?.writableDatabase!!

        //Mapeo de las columnas con valores a insertar
        val values = ContentValues()
        values.put(CriptoContract.Companion.Entrada.COLUMNA_ID, item.id)
        values.put(CriptoContract.Companion.Entrada.COLUMNA_RECURSO, item.recurso)
        values.put(CriptoContract.Companion.Entrada.COLUMNA_PRECIO_RECURSO, item.precioRecurso)

        //Insertar una nueva fila en la tabla
        db.insert(CriptoContract.Companion.Entrada.NOMBRE_TABLA, null, values)

        //Cerrar la conexion
        db.close()
    }

    // Obtiene una cripto partiendo de un id especificado
    fun getCripto(id:String): Cripto {
        var item: Cripto? = null

        //Abrir DB en modo lectura
        val db: SQLiteDatabase = helper?.readableDatabase!!

        //Especificar columnas que quiero consultar
        val columnas = arrayOf(
            CriptoContract.Companion.Entrada.COLUMNA_ID,
            CriptoContract.Companion.Entrada.COLUMNA_RECURSO,
            CriptoContract.Companion.Entrada.COLUMNA_PRECIO_RECURSO
        )

        //Crear un cursor para recorrer la tabla
        val c:Cursor = db.query(
            CriptoContract.Companion.Entrada.NOMBRE_TABLA,
            columnas,
            " id = ?",
            arrayOf(id),
            null,
            null,
            null
        )

        // Se recorre la tabla
        while (c.moveToNext()) {
            item = Cripto(c.getString(c.getColumnIndexOrThrow(CriptoContract.Companion.Entrada.COLUMNA_ID)),
                c.getString(c.getColumnIndexOrThrow(CriptoContract.Companion.Entrada.COLUMNA_RECURSO)),
                c.getDouble(c.getColumnIndexOrThrow(CriptoContract.Companion.Entrada.COLUMNA_PRECIO_RECURSO))
            )
        }

        // Se cierra la conexion y se devuelven los resultados
        c.close()
        return item!!
    }
}