package com.example.taller2_santiagoaviles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taller2_santiagoaviles.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.Camara.setOnClickListener{
            startActivity(Intent(baseContext,CamaraActivity::class.java))

        }
        binding.Contactos.setOnClickListener {
            startActivity(Intent(baseContext,ContactsActivity::class.java))

        }
        binding.Mapa.setOnClickListener {
            startActivity(Intent(baseContext,MapsActivity::class.java))

        }
    }

    /*
    Falta:
    1. Revisar permisos de la actividad contactos
    2. Revisar permisos de la actividad de mapas (Rationale)
    3. Añadir JSONs raw [estilos] a los mapas
     */
}
