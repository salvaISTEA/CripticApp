package com.istea.criptic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.istea.criptic.databinding.ActivitySignInBinding

// Sirve como manejador de los fragmentos utilizados para el registro
class SignInActivity : AppCompatActivity() {

    // Se inicializan las variables con el bind de la activity y la configuracion
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySignInBinding

    // Al crear el Activity, se infla el bind y se anaden los controladores para la navegacion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_sign_in)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    // Sobreescribe la vanegacion hacia el id que especificamos
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_sign_in)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}