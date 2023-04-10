package javeriana.edu.co.taller_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import javeriana.edu.co.taller_1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Click Listener de el evento Juego
        binding.GuessGame.setOnClickListener {
            startActivity(Intent(baseContext, Random_Guess_Activity::class.java).apply {
                putExtra("MaxLimit", binding.InputNumber.text.toString())
                putExtra("Contador", "0")
                putExtra("RandomtoGuess", generateRandom(binding.InputNumber.text.toString()))
                putExtra("Mensaje", "Message: ")
            })
        }
        //Click Listener de el evento Saludar
        binding.Greet.setOnClickListener {
            startActivity(Intent(baseContext,GreetingActivity::class.java).apply{
                putExtra("Idioma", binding.spinner.selectedItem.toString())
            })
        }
        //Click Listener de el evento Paises
        binding.Countries.setOnClickListener {
            startActivity(Intent(baseContext,CountriesActivity::class.java))
        }
    }

    private fun generateRandom(MaxLimit:String): String{
        val rand = (1..MaxLimit.toInt()).random() //Generar numero aleatorio
        return rand.toString()
    }
}