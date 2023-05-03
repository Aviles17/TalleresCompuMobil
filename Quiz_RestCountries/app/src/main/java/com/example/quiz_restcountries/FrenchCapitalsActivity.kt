package com.example.quiz_restcountries

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.quiz_restcountries.databinding.ActivityFrenchCapitalsBinding

class FrenchCapitalsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFrenchCapitalsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrenchCapitalsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadCountries()
    }

    private fun loadCountries() {
        val url = "https://restcountries.com/v3.1/lang/french?fields=name,capital"
        val res = mutableListOf<Pair<String,String>>()

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
                    res.add(name to capital)
                }
                val adapter = ArrayAdapter<Pair<String, String>>(
                    this,
                    R.layout.simple_list_item_1,
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