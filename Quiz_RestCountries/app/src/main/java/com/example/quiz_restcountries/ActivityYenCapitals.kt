package com.example.quiz_restcountries

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.quiz_restcountries.databinding.ActivityYenCapitalsBinding

class ActivityYenCapitals : AppCompatActivity() {
    private lateinit var binding: ActivityYenCapitalsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYenCapitalsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadCountries()
    }


    private fun loadCountries() {
        val url = "https://restcountries.com/v3.1/currency/yen?fields=name,capital,population"
        val res = mutableListOf<Triple<String,String,String>>()

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Handle the API response
                for (i in 0 until response.length()) {
                    val country = response.getJSONObject(i)
                    val name = country.getJSONObject("name").getString("common")
                    val capital = country.getJSONArray("capital").getString(0)
                    val population = country.getInt("population")
                    val tripleta = Triple(name,capital,population.toString())
                    res.add(tripleta)
                }
                val adapter = ArrayAdapter<Triple<String, String,String>>(
                    this,
                    android.R.layout.simple_list_item_1,
                    res.toList()
                )
                binding.listView.adapter = adapter
            },
            { error ->
                // Handle errors
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}