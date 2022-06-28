package com.istea.criptic

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.istea.criptic.databinding.FragmentFirst2Binding

// Fragmento del registro para validar los datos del registro
class SignInFragment : Fragment() {

    // Inicializamos el bind del fragment para tomar los atributos del mismo
    private var _binding: FragmentFirst2Binding? = null
    private val binding get() = _binding!!

    // Valida si el email tiene un formato correcto
    private fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Valida los campos del registro y trata de guardar al usuario
    private fun onRegister(nombreUsuario: String, password: String, email: String) {
        // Variables usadas para validar si el usuario es valido
        var isUsernameValid = false
        var isPasswordValid = false
        var isEmailValid = false

        // Valida la longitud del nombre de usuario
        if (nombreUsuario.length in 4..12) {
            isUsernameValid = true
            binding.warningUsername.visibility = View.INVISIBLE
        } else {
            binding.warningUsername.visibility = View.VISIBLE
        }

        // Valida la longitud de la contrasena
        if (password.length in 6..12) {
            isPasswordValid = true
            binding.warningPassword.visibility = View.INVISIBLE
        } else {
            binding.warningPassword.visibility = View.VISIBLE
        }

        // Valida que el email tenga una estructura adecuada
        if (isEmailValid(email)) {
            isEmailValid = true
            binding.warningEmail.visibility = View.INVISIBLE
        } else {
            binding.warningEmail.visibility = View.VISIBLE
        }

        // Si los campos son validos entonces hacemos la logica para registrarse
        if (isEmailValid && isPasswordValid && isUsernameValid) {
            binding.warningPassword.visibility = View.INVISIBLE
            binding.warningUsername.visibility = View.INVISIBLE
            binding.warningEmail.visibility = View.INVISIBLE
            val usuario = Usuario(
                nombreUsuario,
                email,
                password
            )

            // Trata de obtener el usuario por nombre para ver si ya no existe
            var dataUsuario: Usuario? = null
            try {
                dataUsuario = UsuarioCRUD(context as Context).getUsuario(nombreUsuario)
            } catch (e: Exception) {}

            // Si existe lanza un toast para informar al usuario
            if (dataUsuario != null) {
                Toast.makeText(
                    context as Context,
                    "El Nombre de Usuario ya esta en uso, elija otro",
                    Toast.LENGTH_LONG
                ).show()
            }

            // Trata de obtener al usuario usando el email
            try {
                dataUsuario = UsuarioCRUD(context as Context).getUsuarioByEmail(email)
            } catch(e: Exception) {}

            // Si no lo consigue inserta, si no lanza un toast al usuario
            if (dataUsuario == null) {
                UsuarioCRUD(context as Context).anadirNuevoUsuario(usuario)
                activity?.onBackPressed()
                findNavController().navigate(R.id.action_First2Fragment_to_Second2Fragment)
            } else {
                Toast.makeText(
                    context as Context,
                    "El Email ya esta en uso, elija otro",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Al empezar a crear la vista, hace el bind de los atributos del fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirst2Binding.inflate(inflater, container, false)
        return binding.root

    }

    // Al crear la vista, asigna al boton de registro la funcion de registro
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nombreUsuario = binding.signInUsername.text
        val password = binding.signInPassword.text
        val email = binding.signInEmail.text

        binding.buttonFirst.setOnClickListener {
            onRegister(nombreUsuario.toString(), password.toString(), email.toString())
        }
    }

    // Al destruir la vista elimina el bind
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}