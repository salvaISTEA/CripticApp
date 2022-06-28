package com.istea.criptic

// Representacion de un recurso con los atributos que usamos
class Recurso(asset_id:String, name:String, type_is_crypto:Int, price_usd:Double) {

    // Variables del recurso a obtener, llevan guiones bajos
    var asset_id:String = ""
    var name:String? = null
    var type_is_crypto:Int? = null
    var price_usd:Double? = null

    init {
        this.asset_id = asset_id
        this.name = name
        this.type_is_crypto = type_is_crypto
        this.price_usd = price_usd
    }

}