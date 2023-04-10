package com.example.parcial_1_santiagoaviles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.parcial_1_santiagoaviles.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ListButton.setOnClickListener{
            val InputNumb = binding.TextNumber.text.toString()
            val Numeric_Input = InputNumb.toInt()
            if(Numeric_Input in 0..20){
                startActivity(Intent(baseContext, SucesionNumerosActivity::class.java).apply{
                    putExtra("LimitFor", InputNumb)
                })
            }
            else{
                Toast.makeText(baseContext, "El numero $InputNumb esta fuera de alcance",Toast.LENGTH_LONG).show()
                Log.i("Error_OutBouds", "Numero ingresado afuera de los alcances")
            }
        }

    }
}