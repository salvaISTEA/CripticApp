package com.istea.criptic.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.istea.criptic.*
import com.istea.criptic.databinding.ActivityLoginBinding
import com.istea.criptic.models.Usuario
import java.math.BigInteger
import java.security.MessageDigest

// Actividad para el inicio de sesion
class LoginActivity : AppCompatActivity() {

    // Declara variables necesarias para el modelo y el bind de los atributos del activity
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    // Trata de iniciar
    private fun startMain() {
        val intent = Intent(this, MainActivity::class.java)
        val b = Bundle()
        b.putInt("logged", -1)
        intent.putExtras(b)
        startActivity(intent)
    }

    // Codifica un string dado usando un algoritmo md5
    private fun md5 (input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    // Evalua si el string pasado es un email con un estructura adecuada
    private fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Carga el registri de usuario
    private fun loadSignIn() {
        val myIntent = Intent(
            this,
            SignInActivity::class.java
        )
        startActivity(myIntent)
    }

    // Se anaden los bind necesaios al crear el Activity del Login
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se crea el bing del Activity
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se toman los valores necesarios de la activity usando el binding
        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val signIn = binding.signIn

        // Se toma el view modal para hacer los respectivos bindings
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        // Se anade un observer a los campos de login para las validaciones
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // Deshabilita el boton de login a menos que ambos campos sean correctos
            login.isEnabled = loginState.isDataValid

            // Evalua si el nombre de usuario no es nula
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }

            // Evalua si la contrasena no es nula
            if (loginState.passwordError != null) {
               password.error = getString(loginState.passwordError)
            }
        })

        // Coloca un observer en el resultado del login
        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            // Si existe algun error en el login mostramos el error usando un toast
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }

            // Tomamos de la vista el nombre de usuario y la contrasena
            val nombreUsuario = username.text.toString()
            val passwordUsuario = password.text.toString()

            // Tomamos de la DB el usuario usando el email o su nombre
            var dataUsuario: Usuario? = null
            if (isEmailValid(nombreUsuario)) {
                try {
                    dataUsuario = UsuarioCRUD(this).getUsuarioByEmail(nombreUsuario)
                } catch (e: Exception) {
                }
            } else {
                try {
                    dataUsuario = UsuarioCRUD(this).getUsuario(nombreUsuario)
                } catch (e: Exception) {
                }
            }

            // Si no obtuvimos el usuario de ninguna de las dos maneras levantamos un toast
            if (dataUsuario == null) {
                Toast.makeText(
                    applicationContext,
                    "Usuario no Reconocido",
                    Toast.LENGTH_LONG
                ).show()
                loading.visibility = View.GONE
                setResult(Activity.RESULT_OK)
            } else {
                // Si obtuvimos el usuario, comparamos las contrasenas
                if (dataUsuario.password == md5(passwordUsuario)) {
                    startMain()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    val b = Bundle()
                    b.putInt("logged", 1)
                    intent.putExtras(b)
                    finish()
                } else {
                    // Si falla levantamos un toast
                    Toast.makeText(
                        applicationContext,
                        "Usuario no reconocido",
                        Toast.LENGTH_LONG
                    ).show()
                    loading.visibility = View.GONE
                }
            }
        })

        // Colocamos un observador al nombre de usuario para actualizar su informacion en la vista
        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        // Colocamos un observador en la contrasena para actualizar su informacion en la vista
        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            // Anadimos un evento al hacer click al boton de login
            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }

            // Anadimos un evento al hacer click al boton de registro
            signIn?.setOnClickListener {
                loading.visibility = View.GONE
                loadSignIn()
            }
        }
    }

    // Levanta un toast con un error dado
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}


// Facilita las funciones despues de que los textos han sido cambiados (tipo EditText)
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}