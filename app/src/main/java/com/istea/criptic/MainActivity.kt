package com.istea.criptic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.istea.criptic.databinding.ActivityMainBinding
import com.istea.criptic.models.Cripto
import com.istea.criptic.models.Recurso
import com.istea.criptic.ui.login.LoginActivity
import org.json.JSONObject
import org.w3c.dom.Text
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    // Constantes Usadas para buscar la informacion externa a APIs
    private val precioDolarUrl = "https://api-dolar-argentina.herokuapp.com/api/dolarblue"
    private var precioCriptosUrl = "https://rest.coinapi.io/v1/assets?apiKey=363EDEFB-069A-45F5-BCD5-519713851C1B"
    private var iconosCriptosUrl = "https://rest.coinapi.io/v1/assets/icons?apiKey=363EDEFB-069A-45F5-BCD5-519713851C1B"

    // Crear el bind para usar las propiedades del activity
    private lateinit var binding: ActivityMainBinding

    // Variable del Main con el precio actual del dolar
    private var precioDolar = 0.toDouble()
    private var esPesos: Boolean = true

    // Codigo que se ejecuta al crearse el Main
    override fun onCreate(savedInstanceState: Bundle?) {
        // Se carga el bundle para ver si estamos loggeados
        val b = intent.extras
        if (b == null) {
            loadLogin()
        }

        super.onCreate(savedInstanceState)

        // Se hace el bind del activity
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bind al boton de logout con la logica para el mismo
        val logOut = findViewById<Button>(R.id.logOut)
        val loading = binding.loadingMain
        logOut.setOnClickListener {
            loading.visibility = View.VISIBLE
            logOutProcess()
        }

        // Bind al boton de busqueda con la logica para esta
        val searchButton = findViewById<Button>(R.id.searchButton)
        val search = findViewById<TextView>(R.id.search)
        searchButton.setOnClickListener {
            buscarCriptos(search.text.toString())
        }

        val monedaSwitch = findViewById<Switch>(R.id.switchCurrency)
        monedaSwitch.setOnClickListener {
            switchCurrency()
        }

        // Se cargan los precios necesarios para mostrar los datos en nuestra app
        obtenerPrecioDolar()
        obtenerPreciosCripto()
    }

    // Intercambia la moneda actual y muestra los precios en la que no estaba seleccionada
    private fun switchCurrency() {
        // Cambia el texto a asignar
        var texto = "ARS"
        if (esPesos) {
            texto = "USD"
        }

        // Generamos los arrays a de los TextViews a asignar
        val textosACambiar = arrayOf(
            findViewById<TextView>(R.id.arsText1),
            findViewById<TextView>(R.id.arsText2),
            findViewById<TextView>(R.id.arsText3),
            findViewById<TextView>(R.id.arsText4),
            findViewById<TextView>(R.id.arsText5),
            findViewById<TextView>(R.id.arsText6),
            findViewById<TextView>(R.id.arsText7)
        )
        val valoresACambiar = arrayOf(
            findViewById<TextView>(R.id.valorBTC),
            findViewById<TextView>(R.id.valorETH),
            findViewById<TextView>(R.id.valorXRP),
            findViewById<TextView>(R.id.valorUSDT),
            findViewById<TextView>(R.id.valorADA),
            findViewById<TextView>(R.id.valorDOT),
            findViewById<TextView>(R.id.valorXLM)
        )

        // Iteramos los indices a cambiar
        for (i in textosACambiar.indices) {
            textosACambiar[i].text = texto
            if (!esPesos) {
                // Si no es pesos calculamos en base a pesos
                val valorActualizado =
                    (valoresACambiar[i].text.toString().toDouble() * this.precioDolar)
                valoresACambiar[i].text =
                    ((valorActualizado * 100.0).roundToInt() / 100.0).toString()
            } else {
                // Si es pesos calculamos en base a dolares
                val valorActualizado =
                    (valoresACambiar[i].text.toString().toDouble() / this.precioDolar)
                valoresACambiar[i].text =
                    ((valorActualizado * 100.0).roundToInt() / 100.0).toString()
            }
        }
        // Invertimos si es pesos ya que calculamos el switch
        this.esPesos = !this.esPesos
    }

    // Carga la actividad de login
    private fun loadLogin() {
        val myIntent = Intent(
            this,
            LoginActivity::class.java
        )
        startActivity(myIntent)
    }

    // Ejecuta el proceso de logout
    private fun logOutProcess() {
        val intent = Intent(this, LoginActivity::class.java)
        val b = Bundle()
        b.putInt("logged", -1)
        intent.putExtras(b)
        finish()
        startActivity(intent)
    }

    // Obtiene el precio del dolar actual
    private fun obtenerPrecioDolar() {
        // Textos que se muestran en el main
        val textoValorPeso = findViewById<TextView>(R.id.valorPeso)
        val textoValorUSD = findViewById<TextView>(R.id.valorUSD)

        // Inicializamos Volley y hacemos el pedido a la URL de precios de dolar
        val queue = Volley.newRequestQueue(this)
        val solicitud = StringRequest(Request.Method.GET, this.precioDolarUrl,{
                respuesta ->
            try {
                val jsonObject = JSONObject(respuesta)
                val precioDolar = jsonObject.get("venta").toString()
                val valorDolar = precioDolar
                textoValorPeso.text = valorDolar
                this.precioDolar = precioDolar.toDouble()
                textoValorUSD.text = "${1/valorDolar.toDouble()}"

                // Al terminar de obtener el precio, cargamos los criptos (dependencia)
                cargarCripto()
            } catch (e:Exception) {

            }
        }, Response.ErrorListener {})

        // Anadimos la solicitud a la cola de Volley
        queue.add(solicitud)
    }

    // Obtiene el precio de todos los criptos, se queda con los mas importantes
    private fun obtenerPreciosCripto() {
        // Inicializamos Volley y hacemos el pedido a la API
        val queue = Volley.newRequestQueue(this)
        val solicitud = StringRequest(Request.Method.GET, this.precioCriptosUrl,{
                respuesta ->
            try {
                val typeToken = object : TypeToken<List<Recurso>>() {}.type
                val recursos = Gson().fromJson<List<Recurso>>(respuesta.toString(), typeToken)

                // Iteramos los recursos, ya que queremos filtrar algunos recursos
                for (item in recursos) {
                    // Criptos a filtrar
                    val arr = arrayOf("BTC", "ETH", "XRP", "USDT", "USDC", "BNB", "BUSD", "SOL", "DOGE", "DAI", "TRX", "SHIB", "AVAX", "WBTC", "ADA", "DOT", "XLM")

                    // Si es cripto (type == 1) y es uno de los recursos que necesitamos
                    if (item.type_is_crypto == 1 && item.asset_id in arr) {
                        val cripto = Cripto(item.asset_id, item.name, item.price_usd)

                        var registro: Cripto? = null

                        // Validamos que ya no exista el recurso
                        try {
                            registro = PrecioCriptoCRUD(this).getCripto(item.asset_id)
                        } catch (e: Exception) {}

                        // Si no existe el recurso lo insertamos
                        if (registro == null) {
                            PrecioCriptoCRUD(this).anadirNuevaCripto(cripto)
                        }
                    }
                }

                // Despues de obtener los precios, cargamos los criptos
                cargarCripto()
            } catch (e:Exception) {}
        }, Response.ErrorListener {})

        // Anadimos la solicitud a la cola de Volley
        queue.add(solicitud)
    }

    // Carga las criptos obtenidas a la vista y los pasa a pesos para mostrarlos
    private fun cargarCripto() {
        mostrarRegistros()

        // Lista de criptos a cargar
        val criptoIds = arrayOf("BTC", "ETH", "XRP", "USDT", "ADA", "DOT", "XLM")
        val criptoTextViews = arrayOf(
            findViewById<TextView>(R.id.valorBTC),
            findViewById<TextView>(R.id.valorETH),
            findViewById<TextView>(R.id.valorXRP),
            findViewById<TextView>(R.id.valorUSDT),
            findViewById<TextView>(R.id.valorADA),
            findViewById<TextView>(R.id.valorDOT),
            findViewById<TextView>(R.id.valorXLM)
        )

        for (i in criptoIds.indices) {
            // Valores a operar
            val entradaCripto = PrecioCriptoCRUD(this).getCripto(criptoIds[i])
            val textoValorPeso = findViewById<TextView>(R.id.valorPeso)
            val textoValorUSD = findViewById<TextView>(R.id.valorUSD)

            // Valor por defecto del precio del dolar por si la API esta caida
            if (this.precioDolar < 1) {
                this.precioDolar = 220.00
                textoValorPeso.text = "220.00"
                textoValorUSD.text = "${1/220.00}"
            }

            // Calculo del precio y aproximado
            var precioRecursoPesos = entradaCripto.precioRecurso!!
            if (this.esPesos) {
                precioRecursoPesos *= this.precioDolar
            }
            val aproximado = (precioRecursoPesos * 100.0).roundToInt() / 100.0
            criptoTextViews[i].text = aproximado.toString()
        }
    }

    private fun limpiarRegistros() {
        val criptoTextViews = arrayOf(
            findViewById<TextView>(R.id.valorBTC),
            findViewById<TextView>(R.id.valorETH),
            findViewById<TextView>(R.id.valorXRP),
            findViewById<TextView>(R.id.valorUSDT),
            findViewById<TextView>(R.id.valorADA),
            findViewById<TextView>(R.id.valorDOT),
            findViewById<TextView>(R.id.valorXLM)
        )

        val criptoNames = arrayOf(
            findViewById<TextView>(R.id.textView),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.textView4),
            findViewById<TextView>(R.id.textView5),
            findViewById<TextView>(R.id.textView7),
            findViewById<TextView>(R.id.textView8),
            findViewById<TextView>(R.id.textView9)
        )

        val criptoCurrency = arrayOf(
            findViewById<TextView>(R.id.arsText1),
            findViewById<TextView>(R.id.arsText2),
            findViewById<TextView>(R.id.arsText3),
            findViewById<TextView>(R.id.arsText4),
            findViewById<TextView>(R.id.arsText5),
            findViewById<TextView>(R.id.arsText6),
            findViewById<TextView>(R.id.arsText7)
        )

        for (i in criptoTextViews.indices) {
            criptoTextViews[i].visibility = View.GONE
            criptoCurrency[i].visibility = View.GONE
            criptoNames[i].visibility = View.GONE
        }
    }

    // Mostrar los registros indiferentemente si fueron ocultados
    private fun mostrarRegistros() {
        val criptoTextViews = arrayOf(
            findViewById<TextView>(R.id.valorBTC),
            findViewById<TextView>(R.id.valorETH),
            findViewById<TextView>(R.id.valorXRP),
            findViewById<TextView>(R.id.valorUSDT),
            findViewById<TextView>(R.id.valorADA),
            findViewById<TextView>(R.id.valorDOT),
            findViewById<TextView>(R.id.valorXLM)
        )

        val criptoNames = arrayOf(
            findViewById<TextView>(R.id.textView),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.textView4),
            findViewById<TextView>(R.id.textView5),
            findViewById<TextView>(R.id.textView7),
            findViewById<TextView>(R.id.textView8),
            findViewById<TextView>(R.id.textView9)
        )
        criptoNames[0].text = "BTC"

        val criptoCurrency = arrayOf(
            findViewById<TextView>(R.id.arsText1),
            findViewById<TextView>(R.id.arsText2),
            findViewById<TextView>(R.id.arsText3),
            findViewById<TextView>(R.id.arsText4),
            findViewById<TextView>(R.id.arsText5),
            findViewById<TextView>(R.id.arsText6),
            findViewById<TextView>(R.id.arsText7)
        )

        for (i in criptoTextViews.indices) {
            criptoTextViews[i].visibility = View.VISIBLE
            criptoCurrency[i].visibility = View.VISIBLE
            criptoNames[i].visibility = View.VISIBLE
        }

        val imageView = findViewById<View>(R.id.imageView)
        imageView.visibility = View.VISIBLE
    }

    private fun cargarUnCripto(cripto: Cripto) {
        val precioRecursoPesos = cripto.precioRecurso!! * this.precioDolar
        val aproximado = (precioRecursoPesos * 100.0).roundToInt() / 100.0
        val viewValor = findViewById<TextView>(R.id.valorBTC)
        viewValor.text = aproximado.toString()
        viewValor.visibility = View.VISIBLE
        val viewTexto = findViewById<TextView>(R.id.textView)
        viewTexto.text = cripto.id
        viewTexto.visibility = View.VISIBLE
        val imageView = findViewById<View>(R.id.imageView)
        imageView.visibility = View.INVISIBLE
        val moneda = findViewById<TextView>(R.id.arsText1)
        if (this.esPesos) {
            moneda.text = "ARS"
            moneda.visibility = View.VISIBLE
        } else {
            moneda.text = "USD"
            moneda.visibility = View.VISIBLE
        }
    }

    private fun buscarCriptos(termino: String) {
        limpiarRegistros()
        try {
            val registro = PrecioCriptoCRUD(this).searchCripto(termino)
            cargarUnCripto(registro)
        } catch (e: Exception) {
            cargarCripto()
            return
        }
    }
}