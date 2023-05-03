package com.example.quiz_restcountries

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.quiz_restcountries.databinding.ActivityDisplayPaisBinding

class DisplayPaisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayPaisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayPaisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.Name.text = intent.getStringExtra("NomPais")
        binding.ResCapital.text = intent.getStringExtra("Capital")
        binding.ResSubReg.text = intent.getStringExtra("Subregion")
        binding.ResPupu.text= intent.getStringExtra("population")
        Glide.with(this).load(intent.getStringExtra("flag")).into(binding.imageView)
    }
}