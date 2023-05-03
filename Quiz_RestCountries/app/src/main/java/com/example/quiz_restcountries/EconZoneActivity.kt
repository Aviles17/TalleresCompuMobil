package com.example.quiz_restcountries

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.quiz_restcountries.databinding.ActivityEconZoneBinding
import com.example.quiz_restcountries.model.Pais

class EconZoneActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var binding: ActivityEconZoneBinding
    private lateinit var arrayCountry: ArrayList<Pais>
    private lateinit var arrayNames: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEconZoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var detectarZona= intent.getStringExtra("Zona")
        if (detectarZona != null) {
            detectarZona = detectarZona.lowercase()
            loadCountries(detectarZona)
        }
    }


    private fun loadCountries(zona : String) {
        val url = "https://restcountries.com/v2/regionalbloc/$zona?fields=name,alpha3Code,capital,subregion,population,flags"
        arrayNames = ArrayList<String>()
        arrayCountry = ArrayList<Pais>()

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Handle the API response
                for (i in 0 until response.length()) {
                    val country = response.getJSONObject(i)
                    val name = country.getString("name")
                    val code = country.getString("alpha3Code")
                    val capital = country.getString("capital")
                    val subregion = country.getString("subregion")
                    val population = country.getInt("population")
                    val flag = country.getJSONObject("flags").get("svg").toString()
                    arrayCountry.add(Pais(name,code,capital,subregion,population,flag))
                    arrayNames.add(name)
                }
                val adapterString = ArrayAdapter<String>(this, R.layout.simple_list_item_single_choice, arrayNames)
                binding.listView.adapter = adapterString
                binding.listView.choiceMode = AbsListView.CHOICE_MODE_SINGLE
                binding.listView.onItemClickListener = this
            },
            { error ->
                // Handle errors
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val pais = arrayCountry[position]
        startActivity(Intent(baseContext,DisplayPaisActivity::class.java).apply {
            putExtra("NomPais", pais.name)
            putExtra("Capital", pais.capital)
            putExtra("Subregion", pais.subregion)
            putExtra("population", pais.population.toString())
            putExtra("flag", pais.Flag)
            putExtra("code", pais.code)
        })
    }
}