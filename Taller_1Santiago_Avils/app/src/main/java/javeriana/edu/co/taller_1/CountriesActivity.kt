package javeriana.edu.co.taller_1

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import javeriana.edu.co.taller_1.databinding.ActivityCountriesBinding
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset

class CountriesActivity : AppCompatActivity(), OnItemClickListener{
    private lateinit var binding: ActivityCountriesBinding
    private lateinit var arrayString: ArrayList<String>
    private lateinit var arrayCountry: ArrayList<Country>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Funcion para leer el archivo JSON y ponerlo en String
        JSONLect()
        val adapterString = ArrayAdapter<String>(this, R.layout.simple_list_item_single_choice, arrayString)
        binding.listView.adapter = adapterString
        binding.listView.choiceMode = CHOICE_MODE_SINGLE
        binding.listView.onItemClickListener = this
    }
    private fun loadCountries() : String{
        var json : String
        var istr : InputStream = this.assets.open("paises.json")
        val size = istr.available()
        val byteArray = ByteArray(size)
        istr.read(byteArray)
        istr.close()
        json = String(byteArray, Charset.defaultCharset())
        return json
    }

    private fun JSONLect(){
        arrayString = ArrayList<String>()
        arrayCountry = ArrayList<Country>()
        val jsonObject = JSONObject(loadCountries())
        var arrayCountries = jsonObject.getJSONArray("paises")
        for (i in 0 until arrayCountries.length()){
            val country = arrayCountries.getJSONObject(i)
            val NombrePais = country.get("nombre_pais").toString()
            val ObCountry = Country(
                country.get("nombre_pais").toString(),
                country.get("nombre_pais_int").toString(),
                country.get("sigla").toString(),
                country.get("capital").toString()
            )
            arrayString.add(NombrePais)
            arrayCountry.add(ObCountry)
        }
    }
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val country = arrayCountry[position]
        startActivity(Intent(baseContext,CountryDisplayActivity::class.java).apply{
            putExtra("NomPais", country.NombrePais)
            putExtra("NomIngles", country.NombrePaisENG)
            putExtra("Siglas", country.Sigla)
            putExtra("Capital",country.Capital)
            putExtra("Index",position.toString())
        })
    }
}