package com.example.quiz_restcountries

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quiz_restcountries.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.EuroEcon.setOnClickListener {
            startActivity(Intent(baseContext,EuroZoneActivity::class.java))
        }
        binding.YenCurrency.setOnClickListener {
            startActivity(Intent(baseContext,ActivityYenCapitals::class.java))
        }
        binding.FrenchCap.setOnClickListener{
            startActivity(Intent(baseContext,FrenchCapitalsActivity::class.java))
        }
        binding.EconZone.setOnClickListener {
            startActivity(Intent(baseContext,EconZoneActivity::class.java).apply {
                putExtra("Zona", binding.spinner.selectedItem.toString())
            })
        }
    }
}