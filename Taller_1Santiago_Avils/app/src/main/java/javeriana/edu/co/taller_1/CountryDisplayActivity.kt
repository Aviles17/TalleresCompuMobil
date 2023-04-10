package javeriana.edu.co.taller_1

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javeriana.edu.co.taller_1.databinding.ActivityCountryDisplayBinding
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset

class CountryDisplayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCountryDisplayBinding
    private lateinit var Flags: ArrayList<String>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        JSONlect()
        val index = intent.getStringExtra("Index")?.toInt()
        binding.Bandera.text = index?.let { Flags[it] }
        binding.NombreES.text =intent.getStringExtra("NomPais")
        binding.NombreEN.text = intent.getStringExtra("NomIngles")
        binding.Siglas.text = intent.getStringExtra("Siglas")
        binding.Capital.text = intent.getStringExtra("Capital")
    }

    private fun getEmoji(Unicode1: String, Unicode2: String): String {
        return String(Character.toChars(Integer.decode(Unicode1))) + String(Character.toChars(Integer.decode(Unicode2)))
    }

    private fun loadUnicodes(): String {
        var json: String
        var istr: InputStream = this.assets.open("unicodes.json")
        val size = istr.available()
        val byteArray = ByteArray(size)
        istr.read(byteArray)
        istr.close()
        json = String(byteArray, Charset.defaultCharset())
        return json
    }

    private fun JSONlect() {
        Flags = ArrayList<String>()
        val jsonObject = JSONObject(loadUnicodes())
        var arrayCodigos = jsonObject.getJSONArray("unicodes")
        for (i in 0 until arrayCodigos.length()) {
            val ObjetoBandera = arrayCodigos.getJSONObject(i)
            val UNC1 = ObjetoBandera.get("UNC1").toString()
            val UNC2 = ObjetoBandera.get("UNC2").toString()
            Flags.add(getEmoji(UNC1, UNC2))
        }
    }
}