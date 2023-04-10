package javeriana.edu.co.taller_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javeriana.edu.co.taller_1.databinding.ActivityGreetingBinding

class GreetingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGreetingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGreetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detectaridioma = intent.getStringExtra("Idioma")
        binding.Saludo.text = DeteccionIdioma(detectaridioma.toString())
    }

    private fun DeteccionIdioma(Idioma:String): String{

        if(Idioma == "Spanish"){
            return "Hola y Bienvenido Usuario"
        }
        else if(Idioma == "English"){

            return "Greetings User"
        }
        else if(Idioma == "French"){

            return "Bonjour et bienvenue utilisateur"
        }
        else if(Idioma == "Italian"){

            return "Ciao e Benvenuto Utente"
        }
        else if(Idioma == "Japanese"){

            return "こんにちは、ようこそユーザー"
        }
        else if(Idioma == "Chinese"){

            return "您好，欢迎用户"
        }

        return "Greetings User"
    }

}