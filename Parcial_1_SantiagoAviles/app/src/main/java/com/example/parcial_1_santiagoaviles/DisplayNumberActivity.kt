package com.example.parcial_1_santiagoaviles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parcial_1_santiagoaviles.databinding.ActivityDisplayNumberBinding

class DisplayNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayNumberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val Display = intent.getStringExtra("NumDisplay")

        binding.textView.text = Display

    }
}