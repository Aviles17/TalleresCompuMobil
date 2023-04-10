package javeriana.edu.co.taller_1

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javeriana.edu.co.taller_1.databinding.ActivityRandomGuessBinding

class Random_Guess_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRandomGuessBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityRandomGuessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val MaxLimit = intent.getStringExtra("MaxLimit")
        val Contador = intent.getStringExtra("Contador")
        val NumGuess = intent.getStringExtra("RandomtoGuess")
        var Numeric_Cont = Contador?.toInt()
        val Hint = intent.getStringExtra("Mensaje")

        binding.Prompt.text = "Guess a number between 0 and $MaxLimit"
        binding.Contador.text = "counter: $Contador"
        binding.Pista.text = Hint
        binding.GuessAction.setOnClickListener {
            startActivity(Intent(baseContext, Random_Guess_Activity::class.java).apply {
                putExtra("MaxLimit",MaxLimit)
                Numeric_Cont = Numeric_Cont?.plus(1)
                putExtra("Contador", (Numeric_Cont.toString()))
                putExtra("Mensaje", comparacion(binding.InputGuess.text.toString(),NumGuess.toString()))
                putExtra("RandomtoGuess",NumGuess)
            })
        }
    }
    private fun comparacion(UserInput:String, SystemInt:String): String{

        if(UserInput.toInt() == SystemInt.toInt()){
            return "Message: YOU WIN"
        }
        else if(UserInput.toInt() > SystemInt.toInt()){
            return "Message: $UserInput is bigger"
        }
        else if(UserInput.toInt() < SystemInt.toInt()){
            return "Message: $UserInput is smaller"
        }
        return "Message: "
    }
}