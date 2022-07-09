package com.istea.criptic.models

// Representacion de una cripto con los atributos que usamos
class Cripto(id:String?, recurso:String?, precioRecurso:Double?) {

    var id:String? = null
    var recurso:String? = null
    var precioRecurso:Double? = null

    init {
        this.id = id
        this.recurso = recurso
        this.precioRecurso = precioRecurso
    }

}
