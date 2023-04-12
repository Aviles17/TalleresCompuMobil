package com.example.quiz_restcountries

import android.app.DownloadManager.Request
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.quiz_restcountries.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding : ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

    }


    private fun consumeVoleyEconEURO(){
        val queue = Volley.newRequestQueue(baseContext)
        val REST_COUNTRIES = "h8ps://restcountries.eu/rest/v2/"
        val path = "currency/cop";
        val query = "?fields=name;capital"
        val request = StringRequest(Req)
    }
}