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
import com.example.quiz_restcountries.databinding.ActivityEuroZoneBinding

class EuroZoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEuroZoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEuroZoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadCountries()
    }


    private fun loadCountries() {
        val url = "https://restcountries.com/v2/regionalbloc/eu?fields=name,capital"
        val res = mutableListOf<Pair<String,String>>()

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Handle the API response
                for (i in 0 until response.length()) {
                    val country = response.getJSONObject(i)
                    val name = country.getString("name")
                    val capital = country.getString("capital")
                    res.add(name to capital)
                }
                val adapter = ArrayAdapter<Pair<String, String>>(
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