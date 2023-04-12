package com.example.quiz_restcountries

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quiz_restcountries.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.List.setOnClickListener {
            startActivity(Intent(baseContext,MainActivity2::class.java))
        }
        binding.ZonaEcon.setOnClickListener {
            //Ignore
        }
    }
}